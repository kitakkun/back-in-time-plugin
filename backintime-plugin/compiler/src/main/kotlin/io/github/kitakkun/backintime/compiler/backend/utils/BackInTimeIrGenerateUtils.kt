package io.github.kitakkun.backintime.compiler.backend.utils

import io.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.name.SpecialNames

context(IrBuilderWithScope, BackInTimePluginContext)
fun irCapturePropertyValue(
    ownerClassName: String,
    propertyFqName: String,
    getValueCall: IrCall,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
) = irCall(reportPropertyValueChangeFunctionSymbol).apply {
    putValueArgument(0, irGet(instanceParameter))
    putValueArgument(1, irString(ownerClassName))
    putValueArgument(2, irGet(uuidVariable))
    putValueArgument(3, irString(propertyFqName))
    putValueArgument(4, getValueCall)
}

context(IrBuilderWithScope, BackInTimePluginContext)
fun IrProperty.generateCaptureValueCallForValueContainer(
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
): IrCall? {
    val getter = getter ?: return null
    val valueGetterSymbol = getValueHolderValueGetterSymbol() ?: return null
    return irCapturePropertyValue(
        ownerClassName = parentClassOrNull?.fqNameWhenAvailable?.asString() ?: return null,
        propertyFqName = name.asString(),
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