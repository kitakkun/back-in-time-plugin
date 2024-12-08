package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.getCorrespondingProperty
import com.kitakkun.backintime.compiler.backend.utils.receiver
import com.kitakkun.backintime.compiler.backend.valuecontainer.raw.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.valuecontainer.resolved.ResolvedValueContainer
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable

context(BackInTimePluginContext)
fun IrCall.captureIfNeeded(
    parentClassSymbol: IrClassSymbol,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrExpression? {
    val property = receiver?.getCorrespondingProperty() ?: return null
    val valueContainer = valueContainerClassInfoList.find { it.classSymbol == property.getter?.returnType?.classOrNull } ?: return null
    val captureStrategy = valueContainer.captureTargetSymbols.firstOrNull { it.first == this.symbol }?.second ?: return null

    val ownerClassName = parentClassSymbol.owner.fqNameWhenAvailable?.asString() ?: return null
    val propertyName = property.name.asString()

    val propertyGetterSymbol = property.getter?.symbol ?: return null

    return when (captureStrategy) {
        is CaptureStrategy.AfterCall -> {
            captureAfterCall(
                propertyGetter = propertyGetterSymbol,
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                ownerClassName = ownerClassName,
                propertyName = propertyName,
                valueContainer = valueContainer,
            )
        }

        is CaptureStrategy.ValueArgument -> {
            captureValueArgument(
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                ownerClassName = ownerClassName,
                propertyName = propertyName,
                index = captureStrategy.index,
            )
        }
    }
}

context(BackInTimePluginContext)
private fun IrCall.captureAfterCall(
    classDispatchReceiverParameter: IrValueParameter,
    ownerClassName: String,
    uuidVariable: IrVariable,
    propertyName: String,
    valueContainer: ResolvedValueContainer,
    propertyGetter: IrSimpleFunctionSymbol,
): IrExpression {
    val irBuilder = irBuiltIns.createIrBuilder(symbol)
    return irBuilder.irComposite {
        +this@captureAfterCall
        +irCall(reportPropertyValueChangeFunctionSymbol).apply {
            putValueArgument(0, irGet(classDispatchReceiverParameter))
            putValueArgument(1, irString(ownerClassName))
            putValueArgument(2, irGet(uuidVariable))
            putValueArgument(3, irString(propertyName))
            putValueArgument(
                index = 4,
                valueArgument = when (valueContainer) {
                    is ResolvedValueContainer.Wrapper -> {
                        irCall(valueContainer.getterSymbol).apply {
                            dispatchReceiver = irCall(propertyGetter).apply {
                                dispatchReceiver = irGet(classDispatchReceiverParameter)
                            }
                        }
                    }

                    is ResolvedValueContainer.SelfContained -> {
                        irCall(propertyGetter).apply {
                            dispatchReceiver = irGet(classDispatchReceiverParameter)
                        }
                    }
                },
            )
        }
    }
}

context(BackInTimePluginContext)
private fun IrCall.captureValueArgument(
    index: Int,
    classDispatchReceiverParameter: IrValueParameter,
    ownerClassName: String,
    uuidVariable: IrVariable,
    propertyName: String,
): IrExpression {
    val irBuilder = irBuiltIns.createIrBuilder(symbol)
    val originalValueArgument = getValueArgument(index)

    putValueArgument(
        index = index,
        valueArgument = with(irBuilder) {
            irCall(captureThenReturnValueFunctionSymbol).apply {
                putValueArgument(0, irGet(classDispatchReceiverParameter))
                putValueArgument(1, irString(ownerClassName))
                putValueArgument(2, irGet(uuidVariable))
                putValueArgument(3, irString(propertyName))
                putValueArgument(4, originalValueArgument)
            }
        },
    )
    return this
}
