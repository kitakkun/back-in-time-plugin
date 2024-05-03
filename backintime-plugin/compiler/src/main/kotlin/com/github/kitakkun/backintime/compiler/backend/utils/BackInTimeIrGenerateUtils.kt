package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irGetObject
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.name.SpecialNames

context(IrBuilderWithScope, BackInTimePluginContext)
fun irEmitEventCall(configureEvent: () -> IrExpression): IrCall {
    return irCall(emitEventFunctionSymbol).apply {
        dispatchReceiver = irGetObject(backInTimeServiceClassSymbol)
        putValueArgument(0, configureEvent())
    }
}

context(BackInTimePluginContext, IrBuilderWithScope)
fun irRegisterRelationship(getParentInstance: IrExpression, getChildInstance: IrExpression) =
    irEmitEventCall {
        irCallConstructor(registerRelationshipEventConstructorSymbol, emptyList()).apply {
            putValueArgument(0, getParentInstance)
            putValueArgument(1, getChildInstance)
        }
    }

context(IrBuilderWithScope, BackInTimePluginContext)
@OptIn(UnsafeDuringIrConstructionAPI::class)
fun irCapturePropertyValue(
    propertyName: String,
    getValueCall: IrCall,
    instanceParameter: IrValueParameter,
    uuidVariable: IrVariable,
) = irEmitEventCall {
    irCallConstructor(propertyValueChangeEventConstructorSymbol, emptyList()).apply {
        putValueArgument(0, irGet(instanceParameter))
        putValueArgument(1, irGet(uuidVariable))
        putValueArgument(2, irString(instanceParameter.type.classOrFail.owner.kotlinFqName.asString()))
        putValueArgument(3, irString(propertyName))
        putValueArgument(4, getValueCall)
    }
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

context(BackInTimePluginContext)
fun IrBuilderWithScope.irThrowTypeMismatchException(
    expectedType: String,
    propertyName: String,
) = irThrow(
    irCallConstructor(typeMismatchExceptionConstructor, emptyList()).apply {
        putValueArgument(0, irString(propertyName))
        putValueArgument(1, irString(expectedType))
    },
)

context(BackInTimePluginContext)
fun IrBuilderWithScope.irThrowNoSuchPropertyException(
    parentClassFqName: String,
    propertyNameParameter: IrValueParameter,
) = irThrow(
    irCallConstructor(noSuchPropertyExceptionConstructor, emptyList()).apply {
        putValueArgument(0, irString(parentClassFqName))
        putValueArgument(1, irGet(propertyNameParameter))
    },
)
