package com.kitakkun.backintime.compiler.backend.transformer.implement

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.irPropertySetterCall
import com.kitakkun.backintime.compiler.backend.utils.irValueContainerPropertySetterCall
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import com.kitakkun.backintime.compiler.backend.valuecontainer.ResolvedValueContainer
import com.kitakkun.backintime.compiler.common.BackInTimeConsts
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
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superClass
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
     * generate body for [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.forceSetValue]:
     * ```kotlin
     * fun forceSetValue(propertyOwnerClassFqName: String, propertyName: String, value: Any?) {
     *     if (ownerClassFqName == "com.example.MyClass") {
     *         when (propertyName) {
     *             "prop1" -> prop1 = value
     *             "prop2" -> prop3 = value
     *             ...
     *             else -> throw NoSuchPropertyException(...)
     *         }
     *     } else {
     *         super.forceSetValue(ownerClassFqName, propertyName, value)
     *     }
     * }
     * ```
     */
    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val parentClassReceiver = declaration.dispatchReceiverParameter!!
        val (ownerClassFqNameParameter, propertyNameParameter, valueParameter) = declaration.valueParameters

        val superClassSymbol = parentClass.superClass?.symbol

        return irBuiltIns.createIrBuilder(declaration.symbol).irBlockBody {
            +irIfThenElse(
                type = irBuiltIns.unitType,
                condition = irEquals(irGet(ownerClassFqNameParameter), irString(parentClass.kotlinFqName.asString())),
                thenPart = irWhen(
                    type = irBuiltIns.unitType,
                    branches = parentClass.properties
                        .mapNotNull { property ->
                            irBranch(
                                condition = irEquals(
                                    arg1 = irGet(propertyNameParameter),
                                    arg2 = irString(property.name.asString()),
                                ),
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
                ),
                elsePart = if (superClassSymbol != null && superClassSymbol.owner.isBackInTimeDebuggable) {
                    irCall(
                        callee = declaration,
                        superQualifierSymbol = superClassSymbol,
                    ).apply {
                        dispatchReceiver = irGet(parentClassReceiver)
                        putValueArgument(0, irGet(ownerClassFqNameParameter))
                        putValueArgument(1, irGet(propertyNameParameter))
                        putValueArgument(2, irGet(valueParameter))
                    }
                } else {
                    irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                        putValueArgument(0, irGet(propertyNameParameter))
                        putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                    }
                },
            )
        }
    }

    /**
     * generate body for [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.serializeValue]:
     * ```kotlin
     * override fun serializeValue(propertyOwnerClassFqName: String, propertyName: String, value: Any?): String =
     *     if (ownerClassFqName == "com.example.MyClass") {
     *         when (propertyName) {
     *             "prop1" -> backInTimeJson.encodeToString<String>(value)
     *             "prop2" -> backInTimeJson.encodeToString<Int>(value)
     *             ...
     *             else -> throw NoSuchPropertyException(...)
     *         }
     *     } else {
     *         super.serializeValue(ownerClassFqName, propertyName, value)
     *     }
     * ```
     */
    private fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val (ownerClassFqNameParameter, propertyNameParameter, valueParameter) = declaration.valueParameters
        val irBuilder = irBuiltIns.createIrBuilder(declaration.symbol)

        val superClassSymbol = parentClass.superClass?.symbol
        val parentClassReceiver = declaration.dispatchReceiverParameter!!

        return with(irBuilder) {
            irExprBody(
                irIfThenElse(
                    type = irBuiltIns.stringType,
                    condition = irEquals(irGet(ownerClassFqNameParameter), irString(parentClass.kotlinFqName.asString())),
                    thenPart = irWhen(
                        type = irBuiltIns.stringType,
                        branches = parentClass.properties
                            .mapNotNull { property ->
                                irBranch(
                                    condition = irEquals(
                                        arg1 = irGet(propertyNameParameter),
                                        arg2 = irString(property.name.asString()),
                                    ),
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
                    elsePart = if (superClassSymbol != null && superClassSymbol.owner.isBackInTimeDebuggable) {
                        irCall(
                            callee = declaration,
                            superQualifierSymbol = superClassSymbol,
                        ).apply {
                            dispatchReceiver = irGet(parentClassReceiver)
                            putValueArgument(0, irGet(ownerClassFqNameParameter))
                            putValueArgument(1, irGet(propertyNameParameter))
                            putValueArgument(2, irGet(valueParameter))
                        }
                    } else {
                        irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                            putValueArgument(0, irGet(propertyNameParameter))
                            putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                        }
                    },
                ),
            )
        }
    }

    /**
     * generate body for [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.deserializeValue]:
     * ```kotlin
     * override fun deserializeValue(propertyOwnerClassFqName: String, propertyName: String, value: Any?): String =
     *     if (ownerClassFqName == "com.example.MyClass") {
     *         when (propertyName) {
     *             "prop1" -> backInTimeJson.decodeFromString<String>(value)
     *             "prop2" -> backInTimeJson.decodeFromString<Int>(value)
     *             ...
     *             else -> throw NoSuchPropertyException(...)
     *         }
     *     } else {
     *         super.deserializeValue(ownerClassFqName, propertyName, value)
     *     }
     * ```
     */
    private fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val (ownerClassFqNameParameter, propertyNameParameter, valueParameter) = declaration.valueParameters
        val irBuilder = irBuiltIns.createIrBuilder(declaration.symbol)

        val superClassSymbol = parentClass.superClass?.symbol
        val parentClassReceiver = declaration.dispatchReceiverParameter!!

        return with(irBuilder) {
            irExprBody(
                irIfThenElse(
                    type = irBuiltIns.anyNType,
                    condition = irEquals(irGet(ownerClassFqNameParameter), irString(parentClass.kotlinFqName.asString())),
                    thenPart = irWhen(
                        type = irBuiltIns.anyNType,
                        branches = parentClass.properties.mapNotNull { property ->
                            irBranch(
                                condition = irEquals(
                                    arg1 = irGet(propertyNameParameter),
                                    arg2 = irString(property.name.asString()),
                                ),
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
                    elsePart = if (superClassSymbol != null && superClassSymbol.owner.isBackInTimeDebuggable) {
                        irCall(
                            callee = declaration,
                            superQualifierSymbol = superClassSymbol,
                        ).apply {
                            dispatchReceiver = irGet(parentClassReceiver)
                            putValueArgument(0, irGet(ownerClassFqNameParameter))
                            putValueArgument(1, irGet(propertyNameParameter))
                            putValueArgument(2, irGet(valueParameter))
                        }
                    } else {
                        irCall(throwNoSuchPropertyExceptionFunctionSymbol).apply {
                            putValueArgument(0, irGet(propertyNameParameter))
                            putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                        }
                    },
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
        val classSymbol = type.classOrNull ?: return null
        val correspondingContainerInfo = valueContainerClassInfoList.find { it.classSymbol == classSymbol }

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
        val valueContainerClassInfo = valueContainerClassInfoList.find { it.classSymbol == this.classOrNull } ?: return this

        val typeArguments = (this as? IrSimpleType)?.arguments?.map { it.typeOrFail } ?: return null
        val manuallyConfiguredSerializeType = valueContainerClassInfo.serializeAs?.owner?.typeWith(typeArguments)
        if (manuallyConfiguredSerializeType != null) {
            return manuallyConfiguredSerializeType
        }

        if (valueContainerClassInfo is ResolvedValueContainer.SelfContained) {
            return this
        }

        return typeArguments.firstOrNull()
    }

    /**
     * add a backing field for the [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.backInTimeInstanceUUID] property
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
     * add a backing field for the [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.backInTimeInitializedPropertyMap] property
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
