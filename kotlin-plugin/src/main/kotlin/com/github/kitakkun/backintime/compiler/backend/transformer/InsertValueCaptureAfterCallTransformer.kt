package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.analyzer.ValueHolderStateChangeInsideBodyAnalyzer
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import com.github.kitakkun.backintime.compiler.backend.utils.isValueContainerSetterCall
import org.jetbrains.kotlin.backend.jvm.ir.receiverAndArgs
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
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

        return when {
            expression.isPureSetterCall() -> expression.transformPureSetterCall()
            expression.isValueContainerSetterCall() -> expression.transformValueContainerSetterCall()
            else -> expression.transformComplexCall()
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
        val irBuilder = irBlockBuilder(pluginContext)
        val property = this.symbol.owner.correspondingPropertySymbol?.owner ?: return null
        val propertyGetter = property.getter ?: return null
        return irBuilder.irComposite {
            +this@transformPureSetterCall
            +generateCaptureValueCall(
                propertyName = property.name.asString(),
                getValueCall = irCall(propertyGetter.symbol).apply {
                    this.dispatchReceiver = irGet(classDispatchReceiverParameter)
                },
            )
        }
    }

    /**
     * insert capturing call for value container property
     */
    private fun IrCall.transformValueContainerSetterCall(): IrExpression? {
        val receiver = dispatchReceiver ?: extensionReceiver ?: return null
        val property = (receiver as? IrCall)?.symbol?.owner?.correspondingPropertySymbol?.owner ?: return null
        val irBuilder = irBlockBodyBuilder(pluginContext)
        val getValueCall = with(irBuilder) {
            property.generateValueHolderCaptureCall() ?: return null
        }
        return irBuilder.irComposite {
            +this@transformValueContainerSetterCall
            +getValueCall
        }
    }

    /**
     * insert capturing call for complex call
     * with, apply, let, ...
     */
    private fun IrCall.transformComplexCall(): IrExpression {
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

        val propertiesShouldBeCapturedAfterCall = ValueHolderStateChangeInsideBodyAnalyzer.analyzePropertiesShouldBeCaptured(this)
        val irBuilder = irBlockBuilder(pluginContext)
        val propertyCaptureCalls = propertiesShouldBeCapturedAfterCall.mapNotNull { property ->
            with(irBuilder) {
                property.generateValueHolderCaptureCall()
            }
        }

        if (propertyCaptureCalls.isEmpty()) return this

        return irBuilder.irComposite {
            +this@transformComplexCall
            +propertyCaptureCalls
        }
    }

    context(IrBuilderWithScope)
    private fun generateCaptureValueCall(propertyName: String, getValueCall: IrCall): IrCall {
        return irCall(notifyValueChangeFunctionSymbol).apply {
            dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
            putValueArgument(0, irGet(classDispatchReceiverParameter))
            putValueArgument(1, irString(propertyName))
            putValueArgument(2, getValueCall)
            putValueArgument(3, irGet(uuidVariable))
        }
    }

    context(IrBuilderWithScope)
    private fun IrProperty.generateValueHolderCaptureCall(): IrCall? {
        val propertyGetter = this.getter ?: return null
        val valueGetter = this.getValueHolderValueGetterSymbol() ?: return null
        return generateCaptureValueCall(
            propertyName = name.asString(),
            getValueCall = irCall(valueGetter).apply {
                this.dispatchReceiver = irCall(propertyGetter).apply {
                    this.dispatchReceiver = irGet(classDispatchReceiverParameter)
                }
            }
        )
    }

    private fun IrProperty.getValueHolderValueGetterSymbol(): IrSimpleFunctionSymbol? {
        val propertyClass = getter?.returnType?.classOrNull?.owner ?: return null
        val valueGetterCallableName = valueContainerClassInfoList
            .find { it.classId == propertyClass.classId }
            ?.valueGetter
            ?.callableName ?: return null
        return if (valueGetterCallableName.isGetterName()) {
            propertyClass.getPropertyGetterRecursively(valueGetterCallableName.getPropertyName())
        } else {
            propertyClass.getSimpleFunctionRecursively(valueGetterCallableName.asString())
        }
    }
}
