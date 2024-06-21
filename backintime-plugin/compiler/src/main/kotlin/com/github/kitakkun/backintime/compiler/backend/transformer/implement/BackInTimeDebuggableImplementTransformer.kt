package com.github.kitakkun.backintime.compiler.backend.transformer.implement

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.irPropertySetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.irValueContainerPropertySetterCall
import com.github.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import com.github.kitakkun.backintime.compiler.consts.BackInTimeConsts
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.declarations.addBackingField
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irElseBranch
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irExprBody
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.builders.irIs
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irWhen
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
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

context(BackInTimePluginContext)
class BackInTimeDebuggableImplementTransformer : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (!declaration.isBackInTimeGenerated) return declaration

        when (declaration.name) {
            BackInTimeConsts.forceSetValueMethodName -> {
                declaration.body = generateForceSetPropertyMethodBody(declaration)
            }

            BackInTimeConsts.serializeMethodName -> {
                declaration.body = generateSerializePropertyMethodBody(declaration)
            }

            BackInTimeConsts.deserializeMethodName -> {
                declaration.body = generateDeserializePropertyMethodBody(declaration)
            }
        }

        return declaration
    }

    override fun visitProperty(declaration: IrProperty): IrStatement {
        if (!declaration.isBackInTimeGenerated) return declaration

        when (declaration.name) {
            BackInTimeConsts.backInTimeInstanceUUIDName -> declaration.addBackingFieldOfBackInTimeUUID()
            BackInTimeConsts.backInTimeInitializedPropertyMapName -> declaration.addBackingFieldOfBackInTimeInitializedPropertyMap()
        }

        return declaration
    }

    /**
     * generate body for [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.forceSetValue]
     */
    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val parentClassReceiver = declaration.dispatchReceiverParameter!!
        val (propertyNameParameter, valueParameter) = declaration.valueParameters

        return irBuiltIns.createIrBuilder(declaration.symbol).irBlockBody {
            +irWhen(
                type = irBuiltIns.unitType,
                branches = parentClass.properties.mapNotNull { property ->
                    irBranch(
                        condition = irEquals(irGet(propertyNameParameter), irString(property.name.asString())),
                        result = irSetPropertyValue(
                            parentClassReceiver,
                            property,
                            valueParameter,
                        ) ?: return@mapNotNull null,
                    )
                }.plus(
                    irElseBranch(
                        irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                            putValueArgument(0, irGet(propertyNameParameter))
                            putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                        },
                    ),
                ).toList(),
            )
        }
    }

    /**
     * generate body for [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.serializeValue]
     */
    private fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val (propertyNameParameter, valueParameter) = declaration.valueParameters
        val irBuilder = irBuiltIns.createIrBuilder(declaration.symbol)
        return with(irBuilder) {
            irExprBody(
                irWhen(
                    type = irBuiltIns.stringType,
                    branches = parentClass.properties.mapNotNull { property ->
                        irBranch(
                            condition = irEquals(irGet(propertyNameParameter), irString(property.name.asString())),
                            result = irCall(encodeToStringFunction).apply {
                                extensionReceiver = irCall(backInTimeJsonGetter)
                                putValueArgument(0, irGet(valueParameter))
                                putTypeArgument(index = 0, type = property.getter?.returnType?.getSerializerType() ?: return@mapNotNull null)
                            },
                        )
                    }.plus(
                        irElseBranch(
                            irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                                putValueArgument(0, irGet(propertyNameParameter))
                                putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                            },
                        ),
                    ).toList(),
                ),
            )
        }
    }

    /**
     * generate body for [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.deserializeValue]
     */
    private fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val (propertyNameParameter, valueParameter) = declaration.valueParameters
        val irBuilder = irBuiltIns.createIrBuilder(declaration.symbol)
        return with(irBuilder) {
            irExprBody(
                irWhen(
                    type = irBuiltIns.anyNType,
                    branches = parentClass.properties.mapNotNull { property ->
                        irBranch(
                            condition = irEquals(irGet(propertyNameParameter), irString(property.name.asString())),
                            result = irCall(decodeFromStringFunction).apply {
                                extensionReceiver = irCall(backInTimeJsonGetter)
                                putValueArgument(0, irGet(valueParameter))
                                putTypeArgument(index = 0, type = property.getter?.returnType?.getSerializerType() ?: return@mapNotNull null)
                            },
                        )
                    }.plus(
                        irElseBranch(
                            irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                                putValueArgument(0, irGet(propertyNameParameter))
                                putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                            },
                        ),
                    ).toList(),
                ),
            )
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

    /**
     * add a backing field for the [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.backInTimeInstanceUUID] property
     */
    private fun IrProperty.addBackingFieldOfBackInTimeUUID() {
        val irBuilder = irBuiltIns.createIrBuilder(symbol)
        this.addBackingField {
            this.type = irBuiltIns.stringType
        }.apply {
            initializer = with(irBuilder) {
                irExprBody(irCall(uuidFunctionSymbol))
            }
        }
    }

    /**
     * add a backing field for the [com.github.kitakkun.backintime.runtime.BackInTimeDebuggable.backInTimeInitializedPropertyMap] property
     */
    private fun IrProperty.addBackingFieldOfBackInTimeInitializedPropertyMap() {
        val irBuilder = irBuiltIns.createIrBuilder(symbol)
        this.addBackingField {
            this.type = irBuiltIns.mutableMapClass.typeWith(
                irBuiltIns.stringType,
                irBuiltIns.booleanType,
            )
        }.apply {
            initializer = with(irBuilder) {
                irExprBody(
                    irCall(mutableMapOfFunction).apply {
                        putTypeArgument(0, irBuiltIns.stringType)
                        putTypeArgument(1, irBuiltIns.booleanType)
                    },
                )
            }
        }
    }
}
