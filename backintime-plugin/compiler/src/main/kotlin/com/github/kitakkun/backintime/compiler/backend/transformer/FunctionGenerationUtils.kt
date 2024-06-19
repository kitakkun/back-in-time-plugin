package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.irPropertySetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.irValueContainerPropertySetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.irWhenByProperties
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.builders.irIs
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.parentClassOrNull
import org.jetbrains.kotlin.ir.util.properties

context(BackInTimePluginContext)
internal fun IrSimpleFunction.addBackInTimeDebuggableMethodBody() {
    val parentClass = parentClassOrNull ?: return
    when (name) {
        BackInTimeConsts.forceSetValueMethodName -> {
            body = generateForceSetPropertyMethodBody(this, parentClass)
        }

        BackInTimeConsts.serializeMethodName -> {
            body = generateSerializePropertyMethodBody(this, parentClass)
        }

        BackInTimeConsts.deserializeMethodName -> {
            body = generateDeserializePropertyMethodBody(this, parentClass)
        }
    }
}

/**
 * generate body for [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.forceSetValue]
 */
context(BackInTimePluginContext)
fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBody {
    val parentClassReceiver = declaration.dispatchReceiverParameter!!
    val (propertyNameParameter, valueParameter) = declaration.valueParameters

    with(irBuiltIns.createIrBuilder(declaration.symbol)) {
        return irBlockBody {
            +irWhenByProperties(
                properties = parentClass.properties.toList(),
                propertyNameParameter = propertyNameParameter,
                buildBranchResultExpression = { property ->
                    irSetPropertyValue(
                        parentClassReceiver = parentClassReceiver,
                        property = property,
                        valueParameter = valueParameter,
                    )
                },
                elseBranchExpression = {
                    irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                        putValueArgument(0, irString(parentClass.kotlinFqName.asString()))
                        putValueArgument(1, irGet(it))
                    }
                },
            )
        }
    }
}

/**
 * generate body for [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.serializeValue]
 */
context(BackInTimePluginContext)
internal fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBody {
    val (propertyNameParameter, valueParameter) = declaration.valueParameters
    with(irBuiltIns.createIrBuilder(declaration.symbol)) {
        return irBlockBody {
            +irWhenByProperties(
                properties = parentClass.properties.toList(),
                propertyNameParameter = propertyNameParameter,
                buildBranchResultExpression = { property ->
                    val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                    generateSerializeCall(type = propertyType, valueParameter = valueParameter)
                },
                elseBranchExpression = {
                    irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                        putValueArgument(0, irString(parentClass.kotlinFqName.asString()))
                        putValueArgument(1, irGet(it))
                    }
                },
            )
        }
    }
}

/**
 * generate body for [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.deserializeValue]
 */
context(BackInTimePluginContext)
internal fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBody {
    val (propertyNameParameter, valueParameter) = declaration.valueParameters
    with(irBuiltIns.createIrBuilder(declaration.symbol)) {
        return irBlockBody {
            +irWhenByProperties(
                properties = parentClass.properties.toList(),
                propertyNameParameter = propertyNameParameter,
                buildBranchResultExpression = { property ->
                    val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                    generateDeserializeCall(valueParameter = valueParameter, type = propertyType)
                },
                elseBranchExpression = {
                    irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                        putValueArgument(0, irString(parentClass.kotlinFqName.asString()))
                        putValueArgument(1, irGet(it))
                    }
                },
            )
        }
    }
}

context(BackInTimePluginContext)
private fun IrBuilderWithScope.irSetPropertyValue(
    parentClassReceiver: IrValueParameter,
    property: IrProperty,
    valueParameter: IrValueParameter,
): IrExpression? {
    val type = property.getter?.returnType ?: return null
    val serializerType = property.getter?.returnType?.getSerializerType() ?: return null
    val klass = type.classOrNull?.owner ?: return null
    val correspondingContainerInfo = valueContainerClassInfoList.find { it.classId == klass.classId }

    return irIfThenElse(
        condition = irIs(irGet(valueParameter), serializerType),
        thenPart = irComposite {
            if (correspondingContainerInfo != null) {
                irValueContainerPropertySetterCall(
                    propertyGetter = property.getter!!,
                    dispatchReceiverParameter = parentClassReceiver,
                    valueParameter = valueParameter,
                    valueContainerClassInfo = correspondingContainerInfo,
                )?.let { +it }
            } else if (property.isVar) {
                +irPropertySetterCall(
                    propertySetter = property.setter!!,
                    dispatchReceiverParameter = parentClassReceiver,
                    valueParameter = valueParameter,
                )
            }
        },
        type = pluginContext.irBuiltIns.unitType,
        elsePart = irCall(throwTypeMismatchExceptionFunctionSymbol).apply {
            putValueArgument(0, irString(property.name.asString()))
            putValueArgument(1, irString(serializerType.classFqName?.asString() ?: "unknown"))
        },
    )
}

context(BackInTimePluginContext)
private fun IrBuilderWithScope.generateSerializeCall(valueParameter: IrValueParameter, type: IrType): IrExpression? {
    return irReturn(
        irCall(encodeToStringFunction).apply {
            extensionReceiver = irCall(backInTimeJsonGetter)
            putValueArgument(0, irGet(valueParameter))
            putTypeArgument(index = 0, type = type.getSerializerType() ?: return null)
        },
    )
}

context(BackInTimePluginContext)
private fun IrBuilderWithScope.generateDeserializeCall(valueParameter: IrValueParameter, type: IrType): IrExpression? {
    return irReturn(
        irCall(decodeFromStringFunction).apply {
            extensionReceiver = irCall(backInTimeJsonGetter)
            putValueArgument(0, irGet(valueParameter))
            putTypeArgument(index = 0, type = type.getSerializerType() ?: return null)
        },
    )
}

context(BackInTimePluginContext)
private fun IrType.getSerializerType(): IrType? {
    val valueContainerClassInfo = valueContainerClassInfoList.find { it.classId == this.classOrNull?.owner?.classId }
        ?: return this

    val typeArguments = (this as? IrSimpleType)?.arguments?.map { it.typeOrFail } ?: return null
    val manuallyConfiguredSerializeType = valueContainerClassInfo.serializeAs?.let { referenceClass(it) }?.owner?.typeWith(typeArguments)
    if (manuallyConfiguredSerializeType != null) {
        return manuallyConfiguredSerializeType
    }

    if (valueContainerClassInfo.serializeItSelf) {
        return this
    }

    return typeArguments.firstOrNull()
}
