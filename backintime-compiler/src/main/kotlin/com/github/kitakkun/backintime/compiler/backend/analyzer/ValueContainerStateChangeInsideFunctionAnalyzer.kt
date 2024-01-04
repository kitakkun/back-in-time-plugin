package com.github.kitakkun.backintime.compiler.backend.analyzer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.isValueContainerSetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.util.allParameters
import org.jetbrains.kotlin.ir.util.getArgumentsWithIr
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.ir.visitors.acceptChildrenVoid

/**
 * check if the parameters passed to the target function are changed internally.
 * returns the parameter symbols which may be changed.
 */
context(BackInTimePluginContext)
class ValueContainerStateChangeInsideFunctionAnalyzer private constructor(
    target: IrSimpleFunction,
) : IrElementVisitorVoid {
    companion object {
        context(BackInTimePluginContext)
        private fun analyze(target: IrSimpleFunction): List<IrValueParameter> {
            with(ValueContainerStateChangeInsideFunctionAnalyzer(target)) {
                target.acceptChildrenVoid(this)
                return modifiedParameters
            }
        }

        /**
         * returns the properties which may be changed by the normal function call.
         */
        context(BackInTimePluginContext)
        fun analyzePropertiesShouldBeCaptured(expression: IrCall): Set<IrProperty> {
            val affectedParameters = analyze(expression.symbol.owner)
            val affectedArguments = expression.getArgumentsWithIr()
                .filter { (parameter, _) -> affectedParameters.any { it.symbol == parameter.symbol } }
                .map { (_, argument) -> argument }
            return affectedArguments
                .filterIsInstance<IrCall>()
                .mapNotNull {
                    it.symbol.owner.correspondingPropertySymbol?.owner
                }.toSet()
        }
    }

    private val parameters = target.allParameters

    private val mutableModifiedParameters = mutableListOf<IrValueParameter>()
    val modifiedParameters: List<IrValueParameter> = mutableModifiedParameters

    override fun visitElement(element: IrElement) {
        element.acceptChildrenVoid(this)
    }

    override fun visitCall(expression: IrCall) {
        if (!expression.isValueContainerSetterCall()) return

        val receiverParameter = (expression.receiver as? IrGetValue)?.symbol?.owner as? IrValueParameter ?: return
        val correspondingParameter = parameters.find { it.symbol == receiverParameter.symbol } ?: return

        mutableModifiedParameters.add(correspondingParameter)
    }
}
