package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.name.SpecialNames

context(IrBuilderWithScope, BackInTimePluginContext)
fun irCapturePropertyValue(
    propertyName: String,
    getValueCall: IrCall,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
) = irCall(reportPropertyValueChangeFunctionSymbol).apply {
    putValueArgument(0, irGet(instanceParameter))
    putValueArgument(1, irGet(uuidVariable))
    putValueArgument(2, irString(propertyName))
    putValueArgument(3, getValueCall)
}

context(IrBuilderWithScope, BackInTimePluginContext)
fun IrProperty.generateCaptureValueCallForPureVariable(
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrCall? {
    val getter = getter ?: return null
    return irCapturePropertyValue(
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
    val valueGetterSymbol = getValueHolderValueGetterSymbol() ?: return null
    return irCapturePropertyValue(
        propertyName = name.asString(),
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
    )
}

context(BackInTimePluginContext)
private fun IrProperty.getValueHolderValueGetterSymbol(): IrSimpleFunctionSymbol? {
    val propertyClass = getter?.returnType?.classOrNull?.owner ?: return null
    val valueGetterCallableName = valueContainerClassInfoList
        .find { it.classId == propertyClass.classId }
        ?.getterFunctionName ?: return null
    return when {
        valueGetterCallableName == SpecialNames.THIS -> getter?.symbol
        else -> propertyClass.getSimpleFunctionsRecursively(valueGetterCallableName).firstOrNull { it.owner.valueParameters.isEmpty() }
    }
}

context(BackInTimePluginContext)
val IrClass.hasBackInTimeDebuggableAsInterface
    get() = superTypes.any { it.classFqName == BackInTimeConsts.backInTimeDebuggableInterfaceClassFqName }
