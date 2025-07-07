package com.kitakkun.backintime.compiler.backend.analyzer

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.getCorrespondingProperty
import com.kitakkun.backintime.compiler.backend.utils.isTrackableStateHolderSetterCall
import com.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.util.allParameters
import org.jetbrains.kotlin.ir.util.getArgumentsWithIr
import org.jetbrains.kotlin.ir.visitors.IrVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

/**
 * check if the parameters passed to the target function are changed internally.
 * returns the parameter symbols which may be changed.
 */
class TrackableStateHolderStateChangeInsideFunctionAnalyzer private constructor(
    private val irContext: BackInTimePluginContext,
    target: IrSimpleFunction,
) : IrVisitorVoid() {
    companion object {
        private fun analyze(
            irContext: BackInTimePluginContext,
            target: IrSimpleFunction
        ): List<IrValueParameter> {
            with(TrackableStateHolderStateChangeInsideFunctionAnalyzer(irContext, target)) {
                target.acceptChildrenVoid(this)
                return modifiedParameters
            }
        }

        /**
         * returns the properties which may be changed by the normal function call.
         */
        fun analyzePropertiesShouldBeCaptured(
            irContext: BackInTimePluginContext,
            expression: IrCall
        ): Set<IrProperty> {
            val affectedParameters = analyze(irContext, expression.symbol.owner)
            val affectedArguments = expression.getArgumentsWithIr()
                .filter { (parameter, _) -> affectedParameters.any { it.symbol == parameter.symbol } }
                .map { (_, argument) -> argument }
            return affectedArguments
                .mapNotNull { it.getCorrespondingProperty() }
                .toSet()
        }
    }

    private val parameters = target.allParameters

    private val mutableModifiedParameters = mutableListOf<IrValueParameter>()
    val modifiedParameters: List<IrValueParameter> = mutableModifiedParameters

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitCall(expression: IrCall) {
        if (!expression.isTrackableStateHolderSetterCall(irContext)) return

        val receiverParameter = (expression.receiver as? IrGetValue)?.symbol?.owner as? IrValueParameter ?: return
        val correspondingParameter = parameters.find { it.symbol == receiverParameter.symbol } ?: return

        mutableModifiedParameters.add(correspondingParameter)
    }
}
