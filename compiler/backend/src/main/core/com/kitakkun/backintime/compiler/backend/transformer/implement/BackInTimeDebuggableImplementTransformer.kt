package com.kitakkun.backintime.compiler.backend.transformer.implement

import com.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.kitakkun.backintime.compiler.backend.utils.getSerializerType
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeDebuggable
import com.kitakkun.backintime.compiler.backend.utils.isBackInTimeGenerated
import com.kitakkun.backintime.compiler.backend.utils.signatureForBackInTimeDebugger
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
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

class BackInTimeDebuggableImplementTransformer(
    private val irContext: BackInTimePluginContext,
) : IrElementTransformerVoid() {
    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        if (!declaration.isBackInTimeGenerated) return declaration
        if (declaration.name != BackInTimeConsts.forceSetValueMethodName) return declaration

        declaration.body = generateForceSetPropertyMethodBody(declaration)

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
     * // assume that this function is generated for the class com/example/MyClass
     * fun forceSetValue(propertySignature: String, jsonValue: String) {
     *     when (propertySignature) {
     *         "com/example/MyClass.prop1" -> prop1 = backInTimeJson.decodeFromString(jsonValue)
     *         "com/example/MyClass.prop2" -> prop2 = backInTimeJson.decodeFromString(jsonValue)
     *         else -> super.forceSetValue(propertySignature, jsonValue) // if there's no super class, `throw NoSuchPropertyException(...)`
     *     }
     * }
     * ```
     */
    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction): IrBody {
        val parentClass = declaration.parentAsClass
        val parentClassReceiver = declaration.dispatchReceiverParameter!!
        val (propertySignature, valueParameter) = declaration.valueParameters
        val superClassSymbol = parentClass.superClass?.symbol

        return irContext.irBuiltIns.createIrBuilder(declaration.symbol).irBlockBody {
            +irWhen(
                type = irContext.irBuiltIns.unitType,
                branches = parentClass.properties
                    .mapNotNull { property ->
                        irBranch(
                            condition = irEquals(
                                arg1 = irGet(propertySignature),
                                arg2 = irString(property.signatureForBackInTimeDebugger()),
                            ),
                            result = irSetPropertyValue(
                                parentClassReceiver,
                                property,
                                valueParameter,
                            ) ?: return@mapNotNull null,
                        )
                    }.plus(
                        irElseBranch(
                            if (superClassSymbol != null && superClassSymbol.owner.isBackInTimeDebuggable) {
                                irCall(
                                    callee = declaration,
                                    superQualifierSymbol = superClassSymbol,
                                ).apply {
                                    dispatchReceiver = irGet(parentClassReceiver)
                                    putValueArgument(0, irGet(propertySignature))
                                    putValueArgument(1, irGet(valueParameter))
                                }
                            } else {
                                irCall(irContext.throwNoSuchPropertyExceptionFunctionSymbol).apply {
                                    putValueArgument(0, irGet(propertySignature))
                                    putValueArgument(1, irString(parentClass.kotlinFqName.asString()))
                                }
                            },
                        ),
                    ).toList(),
            )
        }
    }

    private fun IrBuilderWithScope.irSetPropertyValue(
        parentClassReceiver: IrValueParameter,
        property: IrProperty,
        valueParameter: IrValueParameter,
    ): IrExpression? {
        val type = property.getter?.returnType ?: return null
        val classSymbol = type.classOrNull ?: return null
        val correspondingContainerInfo = irContext.valueContainerClassInfoList.find { it.classSymbol == classSymbol }

        val deserializedValue = irCall(irContext.decodeFromStringFunction).apply {
            extensionReceiver = irCall(irContext.backInTimeJsonGetter)
            putValueArgument(0, irGet(valueParameter))
            putTypeArgument(index = 0, type = property.getter?.returnType?.getSerializerType(irContext))
        }

        return if (correspondingContainerInfo != null) {
            val propertyGetterCall = irCall(property.getter!!).apply {
                this.dispatchReceiver = irGet(parentClassReceiver)
            }

            val preSetterCallSymbols = correspondingContainerInfo.setterSymbols.dropLast(1)
            val setterCallSymbol = correspondingContainerInfo.setterSymbols.last()

            val preSetterCalls = preSetterCallSymbols.map { irCall(it).apply { dispatchReceiver = propertyGetterCall } }
            val setterCall = irCall(setterCallSymbol).apply {
                dispatchReceiver = propertyGetterCall
                putValueArgument(0, deserializedValue)
            }

            irComposite {
                +preSetterCalls
                +setterCall
            }
        } else if (property.isVar) {
            irCall(property.setter!!).apply {
                this.dispatchReceiver = irGet(parentClassReceiver)
                putValueArgument(0, deserializedValue)
            }
        } else {
            null
        }
    }

    /**
     * add a backing field for the [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.backInTimeInstanceUUID] property
     */
    private fun IrProperty.addBackingFieldOfBackInTimeUUID() {
        val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
        this.addBackingField {
            this.type = irContext.irBuiltIns.stringType
        }.apply {
            initializer = with(irBuilder) {
                irExprBody(irCall(irContext.uuidFunctionSymbol))
            }
        }
    }

    /**
     * add a backing field for the [com.kitakkun.backintime.core.runtime.BackInTimeDebuggable.backInTimeInitializedPropertyMap] property
     */
    private fun IrProperty.addBackingFieldOfBackInTimeInitializedPropertyMap() {
        val irBuilder = irContext.irBuiltIns.createIrBuilder(symbol)
        this.addBackingField {
            this.type = irContext.irBuiltIns.mutableMapClass.typeWith(
                irContext.irBuiltIns.stringType,
                irContext.irBuiltIns.booleanType,
            )
        }.apply {
            initializer = with(irBuilder) {
                irExprBody(
                    irCall(irContext.mutableMapOfFunction).apply {
                        putTypeArgument(0, irContext.irBuiltIns.stringType)
                        putTypeArgument(1, irContext.irBuiltIns.booleanType)
                    },
                )
            }
        }
    }
}
