package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.irPropertySetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.irThrowNoSuchPropertyException
import com.github.kitakkun.backintime.compiler.backend.utils.irThrowTypeMismatchException
import com.github.kitakkun.backintime.compiler.backend.utils.irValueContainerPropertySetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.irWhenByProperties
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.psi.stubs.impl.serialize

/**
 * generate BackInTimeDebuggable methods' implementations.
 */
context(BackInTimePluginContext)
class ImplementBackInTimeDebuggableMethodsTransformer : IrElementTransformerVoid() {
    private fun shouldGenerateFunctionBody(parentClass: IrClass) = parentClass.superTypes.contains(backInTimeDebuggableInterfaceType)

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return declaration
        if (!shouldGenerateFunctionBody(parentClass)) return declaration

        when (declaration.name) {
            BackInTimeConsts.forceSetValueMethodName -> {
                declaration.body = generateForceSetPropertyMethodBody(declaration, parentClass)
            }

            BackInTimeConsts.serializeMethodName -> {
                declaration.body = generateSerializePropertyMethodBody(declaration, parentClass)
            }

            BackInTimeConsts.deserializeMethodName -> {
                declaration.body = generateDeserializePropertyMethodBody(declaration, parentClass)
            }
        }

        return declaration
    }

    /**
     * generate body for [BackInTimeDebuggable.forceSetValue]
     */
    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBody {
        val parentClassReceiver = declaration.dispatchReceiverParameter!!
        val (propertyNameParameter, valueParameter) = declaration.valueParameters

        with(declaration.irBlockBodyBuilder()) {
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
                        irThrowNoSuchPropertyException(
                            parentClassFqName = parentClass.kotlinFqName.asString(),
                            propertyNameParameter = it,
                        )
                    }
                )
            }
        }
    }

    /**
     * generate body for [BackInTimeDebuggable.serialize]
     */
    private fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBody {
        val (propertyNameParameter, valueParameter) = declaration.valueParameters
        with(declaration.irBlockBodyBuilder()) {
            return irBlockBody {
                +irWhenByProperties(
                    properties = parentClass.properties.toList(),
                    propertyNameParameter = propertyNameParameter,
                    buildBranchResultExpression = { property ->
                        val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                        generateSerializeCall(type = propertyType, valueParameter = valueParameter)
                    },
                    elseBranchExpression = {
                        irThrowNoSuchPropertyException(
                            parentClassFqName = parentClass.kotlinFqName.asString(),
                            propertyNameParameter = it,
                        )
                    }
                )
            }
        }
    }

    /**
     * generate body for [BackInTimeDebuggable.deserialize]
     */
    private fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBody {
        val (propertyNameParameter, valueParameter) = declaration.valueParameters
        with(declaration.irBlockBodyBuilder()) {
            return irBlockBody {
                +irWhenByProperties(
                    properties = parentClass.properties.toList(),
                    propertyNameParameter = propertyNameParameter,
                    buildBranchResultExpression = { property ->
                        val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                        generateDeserializeCall(valueParameter = valueParameter, type = propertyType)
                    },
                    elseBranchExpression = {
                        irThrowNoSuchPropertyException(
                            parentClassFqName = parentClass.kotlinFqName.asString(),
                            propertyNameParameter = it,
                        )
                    }
                )
            }
        }
    }

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
            elsePart = irThrowTypeMismatchException(propertyName = property.name.asString(), expectedType = serializerType.classFqName?.asString() ?: "unknown"),
        )
    }

    private fun IrBuilderWithScope.generateSerializeCall(valueParameter: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(encodeToStringFunction).apply {
                extensionReceiver = irCall(backInTimeJsonGetter)
                putValueArgument(0, irGet(valueParameter))
                putTypeArgument(index = 0, type = type.getSerializerType() ?: return null)
            }
        )
    }

    private fun IrBuilderWithScope.generateDeserializeCall(valueParameter: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(decodeFromStringFunction).apply {
                extensionReceiver = irCall(backInTimeJsonGetter)
                putValueArgument(0, irGet(valueParameter))
                putTypeArgument(index = 0, type = type.getSerializerType() ?: return null)
            }
        )
    }

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
}
