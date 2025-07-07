package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.trackablestateholder.CaptureStrategy
import com.kitakkun.backintime.compiler.backend.trackablestateholder.ResolvedTrackableStateHolder
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetField
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull

fun IrCall.captureIfNeeded(
    irContext: BackInTimePluginContext,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrExpression? {
    val property = receiver?.getCorrespondingProperty() ?: return null
    val trackableStateHolderInfo = irContext.trackableStateHolderClassInfoList.find {
        it.classSymbol == property.getter?.returnType?.classOrNull ||
            it.classSymbol == property.backingField?.type?.classOrNull
    } ?: return null
    val captureStrategy = trackableStateHolderInfo.captureTargetSymbols.firstOrNull { it.first == this.symbol }?.second ?: return null
    val propertySignature = property.signatureForBackInTimeDebugger()

    val propertyGetterSymbol = property.getter?.symbol ?: return null

    return when (captureStrategy) {
        is CaptureStrategy.AfterCall -> {
            val propertyBackingField = property.backingField
            if (propertyBackingField != null) {
                captureAfterCallByFieldAccess(
                    irContext = irContext,
                    classDispatchReceiverParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                    propertySignature = propertySignature,
                    trackableStateHolder = trackableStateHolderInfo,
                    propertyField = propertyBackingField,
                )
            } else {
                captureAfterCallByPropertyGetter(
                    irContext = irContext,
                    classDispatchReceiverParameter = classDispatchReceiverParameter,
                    uuidVariable = uuidVariable,
                    propertySignature = propertySignature,
                    trackableStateHolder = trackableStateHolderInfo,
                    propertyGetter = propertyGetterSymbol,
                )
            }
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

private fun IrCall.captureAfterCallByFieldAccess(
    irContext: BackInTimePluginContext,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertySignature: String,
    trackableStateHolder: ResolvedTrackableStateHolder,
    propertyField: IrField,
): IrExpression {
    val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
    return captureAfterCall(
        irContext = irContext,
        classDispatchReceiverParameter = classDispatchReceiverParameter,
        uuidVariable = uuidVariable,
        propertySignature = propertySignature,
        trackableStateHolder = trackableStateHolder,
        propertyAccessExpression = irBuilder.run {
            irGetField(irGet(classDispatchReceiverParameter), propertyField)
        },
    )
}

private fun IrCall.captureAfterCallByPropertyGetter(
    irContext: BackInTimePluginContext,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertySignature: String,
    trackableStateHolder: ResolvedTrackableStateHolder,
    propertyGetter: IrSimpleFunctionSymbol,
): IrExpression {
    val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
    return captureAfterCall(
        irContext = irContext,
        classDispatchReceiverParameter = classDispatchReceiverParameter,
        uuidVariable = uuidVariable,
        propertySignature = propertySignature,
        trackableStateHolder = trackableStateHolder,
        propertyAccessExpression = irBuilder.run {
            irCall(propertyGetter).apply {
                dispatchReceiver = irGet(classDispatchReceiverParameter)
            }
        },
    )
}

private fun IrCall.captureAfterCall(
    irContext: BackInTimePluginContext,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertySignature: String,
    trackableStateHolder: ResolvedTrackableStateHolder,
    propertyAccessExpression: IrExpression,
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
                valueArgument = when (trackableStateHolder) {
                    is ResolvedTrackableStateHolder.Wrapper -> {
                        irCall(trackableStateHolder.getterSymbol).apply {
                            dispatchReceiver = propertyAccessExpression
                        }
                    }

                    is ResolvedTrackableStateHolder.SelfContained -> {
                        propertyAccessExpression
                    }
                },
            )
            putTypeArgument(
                index = 0,
                type = propertyAccessExpression.type.getSerializerType(irContext),
            )
        }
    }
}

fun captureThenReturn(
    irContext: BackInTimePluginContext,
    irBuilder: IrBuilderWithScope,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
    signature: String,
    value: IrExpression,
    serializerType: IrType?
): IrExpression {
    return irBuilder.run {
        irCall(irContext.captureThenReturnValueFunctionSymbol).apply {
            putValueArgument(0, irGet(instanceParameter))
            putValueArgument(1, irGet(uuidVariable))
            putValueArgument(2, irString(signature))
            putValueArgument(3, value)
            putTypeArgument(0, serializerType)
        }
    }
}

fun IrCall.captureValueArgument(
    irContext: BackInTimePluginContext,
    index: Int,
    classDispatchReceiverParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertySignature: String,
): IrExpression {
    val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
    val originalValueArgument = getValueArgument(index) ?: return this
    val serializerType = originalValueArgument.type.getSerializerType(irContext)

    putValueArgument(
        index = index,
        valueArgument = captureThenReturn(
            irContext = irContext,
            irBuilder = irBuilder,
            instanceParameter = classDispatchReceiverParameter,
            uuidVariable = uuidVariable,
            signature = propertySignature,
            value = originalValueArgument,
            serializerType = serializerType
        ),
    )
    return this
}
