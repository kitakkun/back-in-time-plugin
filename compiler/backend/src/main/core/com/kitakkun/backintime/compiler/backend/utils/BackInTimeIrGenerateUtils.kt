package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedTrackableStateHolder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull

private fun irCapturePropertyValue(
    irContext: BackInTimePluginContext,
    irBuilder: IrBuilderWithScope,
    propertySignature: String,
    getValueCall: IrCall,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertyType: IrType,
) = irBuilder.irCall(irContext.reportPropertyValueChangeFunctionSymbol).apply {
    putValueArgument(0, irBuilder.irGet(instanceParameter))
    putValueArgument(1, irBuilder.irGet(uuidVariable))
    putValueArgument(2, irBuilder.irString(propertySignature))
    putValueArgument(3, getValueCall)
    putTypeArgument(0, propertyType.getSerializerType(irContext))
}

fun IrProperty.generateCaptureValueCallForValueContainer(
    irContext: BackInTimePluginContext,
    irBuilder: IrBuilderWithScope,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrCall? {
    val getter = getter ?: return null
    val valueGetterSymbol = getValueHolderValueGetterSymbol(irContext) ?: return null
    return irCapturePropertyValue(
        irContext = irContext,
        irBuilder = irBuilder,
        propertySignature = signatureForBackInTimeDebugger(),
        getValueCall = with(irBuilder) {
            if (valueGetterSymbol == getter.symbol) {
                irCall(getter.symbol).apply {
                    dispatchReceiver = irGet(instanceParameter)
                }
            } else {
                irCall(valueGetterSymbol).apply {
                    dispatchReceiver = irCall(getter).apply {
                        dispatchReceiver = irGet(instanceParameter)
                    }
                }
            }
        },
        instanceParameter = instanceParameter,
        uuidVariable = uuidVariable,
        propertyType = getter.returnType,
    )
}

private fun IrProperty.getValueHolderValueGetterSymbol(
    irContext: BackInTimePluginContext,
): IrSimpleFunctionSymbol? {
    val propertyGetter = getter ?: return null
    val propertyClassSymbol = propertyGetter.returnType.classOrNull ?: return null
    val valueContainerInfo = irContext.valueContainerClassInfoList.find { it.classSymbol == propertyClassSymbol } ?: return null
    return when (valueContainerInfo) {
        is ResolvedTrackableStateHolder.SelfContained -> propertyGetter.symbol
        is ResolvedTrackableStateHolder.Wrapper -> valueContainerInfo.getterSymbol
    }
}
