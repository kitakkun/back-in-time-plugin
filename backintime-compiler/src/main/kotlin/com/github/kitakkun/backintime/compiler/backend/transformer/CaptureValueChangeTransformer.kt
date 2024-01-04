package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.analyzer.ValueContainerStateChangeInsideFunctionAnalyzer
import com.github.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForPureVariable
import com.github.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForValueContainer
import com.github.kitakkun.backintime.compiler.backend.utils.getRelevantLambdaExpressions
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isIndirectValueContainerSetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.isLambdaFunctionRelevantCall
import com.github.kitakkun.backintime.compiler.backend.utils.isValueContainerSetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

/**
 * capture state changes of properties defined in [parentClassSymbol]
 */
context(BackInTimePluginContext)
class CaptureValueChangeTransformer(
    private val parentClassSymbol: IrClassSymbol,
    private val classDispatchReceiverParameter: IrValueParameter,
    private val uuidVariable: IrVariable,
) : IrElementTransformerVoid() {
    override fun visitElement(element: IrElement): IrElement {
        element.transformChildrenVoid(this)
        return element
    }

    override fun visitCall(expression: IrCall): IrExpression {
        expression.transformChildrenVoid(this)

        return when {
            expression.isPureSetterCall() -> expression.transformPureSetterCall()
            expression.isValueContainerRelevantCall() -> expression.transformValueContainerRelevantCall()
            else -> expression
        } ?: expression
    }

    private fun IrCall.isPureSetterCall(): Boolean {
        val propertySymbol = this.symbol.owner.correspondingPropertySymbol?.owner ?: return false
        return this.symbol.owner.isSetter && propertySymbol.parentClassOrNull?.symbol == parentClassSymbol
    }

    private fun IrCall.isValueContainerRelevantCall(): Boolean {
        return receiverAndArgs()
            .filterIsInstance<IrCall>()
            .mapNotNull { it.symbol.owner.correspondingPropertySymbol?.owner }
            .any { property ->
                property.parentClassOrNull?.symbol == parentClassSymbol && valueContainerClassInfoList.any { it.classId == property.getter?.returnType?.classOrNull?.owner?.classId }
            }
    }

    private fun IrCall.transformPureSetterCall(): IrExpression? {
        val property = this.symbol.owner.correspondingPropertySymbol?.owner ?: return null
        with(irBlockBuilder()) {
            val captureCall = property.generateCaptureValueCallForPureVariable(
                instanceParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
            ) ?: return null
            return irComposite {
                +this@transformPureSetterCall
                +captureCall
            }
        }
    }

    private fun IrCall.transformValueContainerRelevantCall(): IrExpression {
        if (isLambdaFunctionRelevantCall()) {
            transformInsideRelevantLambdaFunctions()
        }

        if (isValueContainerSetterCall()) {
            return transformValueContainerSetterCall() ?: this
        }

        if (isIndirectValueContainerSetterCall()) {
            return transformIndirectValueContainerSetterCall()
        }

        return this
    }

    private fun IrCall.transformInsideRelevantLambdaFunctions() {
        val involvingLambdas = getRelevantLambdaExpressions()

        val passedProperties = receiverAndArgs()
            .filterIsInstance<IrCall>()
            .mapNotNull { it.symbol.owner.correspondingPropertySymbol?.owner }
            .filter { it.parentClassOrNull?.symbol == parentClassSymbol }
            .toSet()

        involvingLambdas.forEach { lambda ->
            lambda.transformChildrenVoid(
                LambdaArgumentBodyTransformer(
                    passedProperties = passedProperties,
                    classDispatchReceiverParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                )
            )
        }
    }

    private fun IrCall.transformValueContainerSetterCall(): IrExpression? {
        val property = (receiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return null
        with(irBlockBuilder()) {
            val getValueCall = property.generateCaptureValueCallForValueContainer(
                instanceParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
            ) ?: return null
            return irComposite {
                +this@transformValueContainerSetterCall
                +getValueCall
            }
        }
    }

    private fun IrCall.transformIndirectValueContainerSetterCall(): IrExpression {
        val propertiesShouldBeCapturedAfterCall = ValueContainerStateChangeInsideFunctionAnalyzer.analyzePropertiesShouldBeCaptured(this)
        with(irBlockBuilder()) {
            val propertyCaptureCalls = propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
                property.generateCaptureValueCallForValueContainer(
                    instanceParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                )
            }

            if (propertyCaptureCalls.isEmpty()) return this@transformIndirectValueContainerSetterCall

            return irComposite {
                +this@transformIndirectValueContainerSetterCall
                +propertyCaptureCalls
            }
        }
    }
}
