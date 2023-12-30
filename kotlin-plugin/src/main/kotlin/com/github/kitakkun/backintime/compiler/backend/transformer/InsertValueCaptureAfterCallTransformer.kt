package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.analyzer.ValueHolderStateChangeInsideBodyAnalyzer
import com.github.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForPureVariable
import com.github.kitakkun.backintime.compiler.backend.utils.generateCaptureValueCallForValueContainer
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isIndirectValueContainerSetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.isValueContainerSetterCall
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.isSetter
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class InsertValueCaptureAfterCallTransformer(
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

        expression.transformValueContainerSetterCallInsideLambdaArguments()
        return when {
            expression.isPureSetterCall() -> expression.transformPureSetterCall()
            expression.isValueContainerSetterCall() -> expression.transformValueContainerSetterCall()
            expression.isIndirectValueContainerSetterCall() -> expression.transformIndirectValueContainerSetterCall()
            else -> expression
        } ?: expression
    }

    private fun IrCall.isPureSetterCall(): Boolean {
        val propertySymbol = this.symbol.owner.correspondingPropertySymbol?.owner ?: return false
        return this.symbol.owner.isSetter && propertySymbol.parentClassOrNull?.symbol == parentClassSymbol
    }

    /**
     * insert capturing call for pure variable property
     */
    private fun IrCall.transformPureSetterCall(): IrExpression? {
        val irBuilder = irBlockBuilder()
        val property = this.symbol.owner.correspondingPropertySymbol?.owner ?: return null
        val captureCall = with(irBuilder) {
            property.generateCaptureValueCallForPureVariable(
                instanceParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
            )
        } ?: return null
        return irBuilder.irComposite {
            +this@transformPureSetterCall
            +captureCall
        }
    }

    /**
     * insert capturing call for value container property
     */
    private fun IrCall.transformValueContainerSetterCall(): IrExpression? {
        val receiver = dispatchReceiver ?: extensionReceiver ?: return null
        val property = (receiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return null
        val irBuilder = irBlockBodyBuilder()
        val getValueCall = with(irBuilder) {
            property.generateCaptureValueCallForValueContainer(
                instanceParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
            )
        } ?: return null
        return irBuilder.irComposite {
            +this@transformValueContainerSetterCall
            +getValueCall
        }
    }

    /**
     * insert capturing call for indirect value container property
     */
    private fun IrCall.transformIndirectValueContainerSetterCall(): IrExpression {
        val propertiesShouldBeCapturedAfterCall = ValueHolderStateChangeInsideBodyAnalyzer.analyzePropertiesShouldBeCaptured(this)
        val irBuilder = irBlockBuilder()
        val propertyCaptureCalls =
            propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
                with(irBuilder) {
                    property.generateCaptureValueCallForValueContainer(
                        instanceParameter = classDispatchReceiverParameter,
                        uuidVariable = uuidVariable,
                    )
                }
            }

        if (propertyCaptureCalls.isEmpty()) return this

        return irBuilder.irComposite {
            +this@transformIndirectValueContainerSetterCall
            +propertyCaptureCalls
        }
    }

    /**
     * insert capturing call for complex call
     * with, apply, let, ...
     */
    private fun IrCall.transformValueContainerSetterCallInsideLambdaArguments() {
        // ラムダ式の中でプロパティの値変更処理が発生する場合，その後ろに比較→キャプチャを挿入する
        val passedProperties = receiverAndArgs()
            .filterIsInstance<IrCall>()
            .mapNotNull { it.symbol.owner.correspondingPropertySymbol?.owner }
            .toSet()
        val lambdas = this.valueArguments.filterIsInstance<IrFunctionExpression>()
        lambdas.forEach { lambda ->
            lambda.transformChildrenVoid(
                LambdaArgumentBodyTransformer(
                    passedProperties = passedProperties,
                    classDispatchReceiverParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                )
            )
        }
    }
}
