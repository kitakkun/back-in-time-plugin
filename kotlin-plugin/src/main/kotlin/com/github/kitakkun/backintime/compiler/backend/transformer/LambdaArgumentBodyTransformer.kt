package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyGetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isGetterName
import com.github.kitakkun.backintime.compiler.backend.utils.receiver
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irIfThen
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

context(BackInTimePluginContext)
class LambdaArgumentBodyTransformer(
    private val passedProperties: Set<IrProperty>,
    private val classDispatchReceiverParameter: IrValueParameter,
    private val uuidVariable: IrVariable,
) : IrElementTransformerVoid() {
    override fun visitElement(element: IrElement): IrElement {
        element.transformChildrenVoid(this)
        return element
    }

    override fun visitCall(expression: IrCall): IrExpression {
        val receiver = expression.receiver ?: return expression
        val receiverClassId = receiver.type.classOrNull?.owner?.classId
        val callingFunctionName = expression.symbol.owner.name

        val valueContainerClassInfo = valueContainerClassInfoList.find { it.classId == receiverClassId }
        val captureTargetFunctionNames = valueContainerClassInfo?.capturedCallableIds?.map { it.callableName }.orEmpty()
        if (callingFunctionName !in captureTargetFunctionNames) return expression

        val possibleReceiverProperties = passedProperties.filter { it.getter?.returnType?.classOrNull?.owner?.classId == receiverClassId }
        val irBuilder = expression.irBlockBodyBuilder(pluginContext)
        val captureCalls = possibleReceiverProperties.mapNotNull { property ->
            with(irBuilder) {
                val propertyGetter = property.getter ?: return@mapNotNull null
                val captureCall = property.generateValueHolderCaptureCall() ?: return@mapNotNull null
                val getPropertyInstanceCall = irCall(propertyGetter).apply {
                    dispatchReceiver = irGet(classDispatchReceiverParameter)
                }
                irIfThen(
                    condition = irEquals(receiver, getPropertyInstanceCall),
                    thenPart = captureCall,
                    type = pluginContext.irBuiltIns.unitType,
                )
            }
        }

        if (captureCalls.isEmpty()) return expression

        return irBuilder.irComposite {
            +expression
            +captureCalls
        }
    }

    // FIXME: 重複コード
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
