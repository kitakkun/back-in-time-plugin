package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId

context(IrBuilderWithScope, BackInTimePluginContext)
fun generateCaptureValueCall(
    propertyName: String,
    getValueCall: IrCall,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
) = irCall(notifyValueChangeFunctionSymbol).apply {
    dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
    putValueArgument(0, irGet(instanceParameter))
    putValueArgument(1, irString(propertyName))
    putValueArgument(2, getValueCall)
    putValueArgument(3, irGet(uuidVariable))
}

context(IrBuilderWithScope, BackInTimePluginContext)
fun IrProperty.generateCaptureValueCallForPureVariable(
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrCall? {
    val getter = getter ?: return null
    return generateCaptureValueCall(
        propertyName = name.asString(),
        getValueCall = irCall(getter.symbol).apply {
            this.dispatchReceiver = irGet(instanceParameter)
        },
        instanceParameter = instanceParameter,
        uuidVariable = uuidVariable,
    )
}

context(IrBuilderWithScope, BackInTimePluginContext)
fun IrProperty.generateCaptureValueCallForValueContainer(
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrCall? {
    val getter = getter ?: return null
    val valueGetter = getValueHolderValueGetterSymbol() ?: return null
    return generateCaptureValueCall(
        propertyName = name.asString(),
        getValueCall = irCall(valueGetter).apply {
            dispatchReceiver = irCall(getter).apply {
                dispatchReceiver = irGet(instanceParameter)
            }
        },
        instanceParameter = instanceParameter,
        uuidVariable = uuidVariable,
    )
}

context(BackInTimePluginContext)
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
