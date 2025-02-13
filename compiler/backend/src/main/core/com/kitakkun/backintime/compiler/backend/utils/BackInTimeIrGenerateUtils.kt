package com.kitakkun.backintime.compiler.backend.utils

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
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

context(IrBuilderWithScope, BackInTimePluginContext)
private fun irCapturePropertyValue(
    propertySignature: String,
    getValueCall: IrCall,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
    propertyType: IrType,
) = irCall(reportPropertyValueChangeFunctionSymbol).apply {
    putValueArgument(0, irGet(instanceParameter))
    putValueArgument(1, irGet(uuidVariable))
    putValueArgument(2, irString(propertySignature))
    putValueArgument(3, getValueCall)
    putTypeArgument(0, propertyType.getSerializerType())
}

context(IrBuilderWithScope, BackInTimePluginContext)
fun IrProperty.generateCaptureValueCallForValueContainer(
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrCall? {
    val getter = getter ?: return null
    val valueGetterSymbol = getValueHolderValueGetterSymbol() ?: return null
    return irCapturePropertyValue(
        propertySignature = signatureForBackInTimeDebugger(),
        getValueCall = if (valueGetterSymbol == getter.symbol) {
            irCall(getter.symbol).apply {
                dispatchReceiver = irGet(instanceParameter)
            }
        } else {
            irCall(valueGetterSymbol).apply {
                dispatchReceiver = irCall(getter).apply {
                    dispatchReceiver = irGet(instanceParameter)
                }
            }
        },
        instanceParameter = instanceParameter,
        uuidVariable = uuidVariable,
        propertyType = getter.returnType,
    )
}

context(BackInTimePluginContext)
private fun IrProperty.getValueHolderValueGetterSymbol(): IrSimpleFunctionSymbol? {
    val propertyGetter = getter ?: return null
    val propertyClassSymbol = propertyGetter.returnType.classOrNull ?: return null
    val valueContainerInfo = valueContainerClassInfoList.find { it.classSymbol == propertyClassSymbol } ?: return null
    return when (valueContainerInfo) {
        is ResolvedValueContainer.SelfContained -> propertyGetter.symbol
        is ResolvedValueContainer.Wrapper -> valueContainerInfo.getterSymbol
    }
}
