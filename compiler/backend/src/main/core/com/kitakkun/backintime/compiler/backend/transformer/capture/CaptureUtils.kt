package com.kitakkun.backintime.compiler.backend.transformer.capture

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.getCorrespondingProperty
import com.kitakkun.backintime.compiler.backend.utils.getSerializerType
import com.kitakkun.backintime.compiler.backend.utils.receiver
import com.kitakkun.backintime.compiler.backend.utils.signatureForBackInTimeDebugger
import com.kitakkun.backintime.compiler.backend.valuecontainer.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull

fun IrCall.captureIfNeeded(
    irContext: BackInTimePluginContext,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrExpression? {
    val property = receiver?.getCorrespondingProperty() ?: return null
    val valueContainer = irContext.valueContainerClassInfoList.find { it.classSymbol == property.getter?.returnType?.classOrNull } ?: return null
    val captureStrategy = valueContainer.captureTargetSymbols.firstOrNull { it.first == this.symbol }?.second ?: return null
    val propertySignature = property.signatureForBackInTimeDebugger()

    val propertyGetterSymbol = property.getter?.symbol ?: return null

    return when (captureStrategy) {
        is CaptureStrategy.AfterCall -> {
            captureAfterCall(
                irContext = irContext,
                propertyGetter = propertyGetterSymbol,
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                propertySignature = propertySignature,
                valueContainer = valueContainer,
            )
        }

        is CaptureStrategy.ValueArgument -> {
            captureValueArgument(
                irContext = irContext,
                classDispatchReceiverParameter = classDispatchReceiverParameter,
                uuidVariable = uuidVariable,
                propertySignature = propertySignature,
                index = captureStrategy.index,
            )
        }
    }
}

private fun IrCall.captureAfterCall(
    irContext: BackInTimePluginContext,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertySignature: String,
    valueContainer: ResolvedValueContainer,
    propertyGetter: IrSimpleFunctionSymbol,
): IrExpression {
    val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
    return irBuilder.irComposite {
        +this@captureAfterCall
        +irCall(irContext.reportPropertyValueChangeFunctionSymbol).apply {
            putValueArgument(0, irGet(classDispatchReceiverParameter))
            putValueArgument(1, irGet(uuidVariable))
            putValueArgument(2, irString(propertySignature))
            putValueArgument(
                index = 3,
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
            putTypeArgument(
                index = 0,
                type = propertyGetter.owner.returnType.getSerializerType(irContext),
            )
        }
    }
}

private fun IrCall.captureValueArgument(
    irContext: BackInTimePluginContext,
    index: Int,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertySignature: String,
): IrExpression {
    val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
    val originalValueArgument = getValueArgument(index)

    putValueArgument(
        index = index,
        valueArgument = with(irBuilder) {
            irCall(irContext.captureThenReturnValueFunctionSymbol).apply {
                putValueArgument(0, irGet(classDispatchReceiverParameter))
                putValueArgument(1, irGet(uuidVariable))
                putValueArgument(2, irString(propertySignature))
                putValueArgument(3, originalValueArgument)
                originalValueArgument?.type?.getSerializerType(irContext)?.let {
                    putTypeArgument(0, it)
                }
            }
        },
    )
    return this
}
