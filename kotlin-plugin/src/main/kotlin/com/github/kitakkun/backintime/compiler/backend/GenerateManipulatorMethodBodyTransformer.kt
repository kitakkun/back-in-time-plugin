package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.isSetterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.isReifiable
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

/**
 * generate DebuggableStateHolderManipulator methods bodies
 */
class GenerateManipulatorMethodBodyTransformer(
    private val pluginContext: IrPluginContext,
    private val valueContainerClassInfoList: List<ValueContainerClassInfo>,
) : IrElementTransformerVoid() {
    // reference val backInTimeJson = Json { ... }
    private val json = pluginContext.referenceProperties(BackInTimeConsts.myJsonPropertyId).single().owner
    private val encodeToStringFunction = pluginContext.referenceFunctions(CallableId(FqName("kotlinx.serialization"), Name.identifier("encodeToString"))).first {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    }
    private val decodeFromStringFunction = pluginContext.referenceFunctions(CallableId(FqName("kotlinx.serialization"), Name.identifier("decodeFromString"))).first {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    }

    private val backInTimeRuntimeException = pluginContext.referenceClass(BackInTimeConsts.backInTimeRuntimeExceptionClassId)!!
    private val nullValueNotAssignableExceptionConstructor = backInTimeRuntimeException.owner.sealedSubclasses
        .first { it.owner.classId == BackInTimeConsts.nullValueNotAssignableExceptionClassId }.constructors.first()

    private fun shouldGenerateFunctionBody(parentClass: IrClass): Boolean {
        return parentClass.superTypes.any { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!shouldGenerateFunctionBody(parentClass)) return super.visitSimpleFunction(declaration)

        with(IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        )) {
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
        }

        return super.visitFunction(declaration)
    }

    context(IrBlockBodyBuilder)
    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBlockBody {
        return blockBody {
            val propertyNameValueParameter = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = pluginContext.irBuiltIns.unitType,
                branches = parentClass.properties
                    .filter { it.backingField != null }
                    .mapNotNull { property ->
                        irBranch(
                            condition = irEquals(irGet(propertyNameValueParameter), irString(property.name.asString())),
                            result = generateSetForProperty(
                                parentClassReceiver = declaration.dispatchReceiverParameter!!,
                                property = property,
                                type = property.backingField!!.type,
                                value = value) ?: return@mapNotNull null,
                        )
                    }.toList() + irElseBranch(irBlock {}),
            )
        }
    }

    private fun IrBuilderWithScope.generateSetForProperty(
        parentClassReceiver: IrValueParameter,
        property: IrProperty,
        type: IrType,
        value: IrValueParameter,
    ): IrExpression? {
        if (!type.isValueContainer() && property.isVar) {
            val setter = property.setter ?: return null
            return irCall(setter).apply {
                dispatchReceiver = irGet(parentClassReceiver)
                putValueArgument(0, irGet(value))
            }
        }

        val valueContainerClassInfo = valueContainerClassInfoList.firstOrNull { it.classId == type.classOrNull?.owner?.classId } ?: return null
        val setterCallableId = valueContainerClassInfo.valueSetter
        val valueSetter = if (setterCallableId.callableName.isSetterName()) {
            type.classOrNull?.getPropertySetter(setterCallableId.callableName.getPropertyName())
        } else {
            pluginContext.referenceFunctions(setterCallableId).firstOrNull()
        } ?: return null

        val propertyGetter = property.getter ?: return null

        return irCall(valueSetter).apply {
            dispatchReceiver = irCall(propertyGetter).apply {
                dispatchReceiver = irGet(parentClassReceiver)
            }
            putValueArgument(0, irGet(value))
        }
    }

    private fun IrType.isValueContainer(): Boolean {
        return valueContainerClassInfoList.any { it.classId == this.classOrNull?.owner?.classId }
    }

    /**
     * generate body for [DebuggableStateHolderManipulator.serializePropertyForBackInDebug]
     */
    context(IrBlockBodyBuilder)
    private fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBlockBody {
        return blockBody {
            val propertyNameValueParameter = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = pluginContext.irBuiltIns.unitType,
                branches = parentClass.properties
                    .filter { it.backingField != null }
                    .map { it.name.asString() to it.backingField!!.type }
                    .mapNotNull { (propertyName, type) ->
                        irBranch(
                            condition = irEquals(irGet(propertyNameValueParameter), irString(propertyName)),
                            result = generateSerializeCall(type = type, value = value) ?: return@mapNotNull null,
                        )
                    }.toList() + irElseBranch(irReturn(irString("NONE"))), // FIXME: should throw an exception
            )
        }
    }

    private fun IrBuilderWithScope.generateSerializeCall(value: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(encodeToStringFunction).apply {
                extensionReceiver = irCall(json.getter!!)
                putValueArgument(0, irGet(value))
                putTypeArgument(0, if (type.isValueContainer()) {
                    (type as? IrSimpleType)?.arguments?.firstOrNull()?.typeOrNull ?: return null
                } else {
                    type
                })
            }
        )
    }

    /**
     * generate body for [DebuggableStateHolderManipulator.deserializePropertyForBackInDebug]
     */
    context(IrBlockBodyBuilder)
    private fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBlockBody {
        return blockBody {
            val propertyNameValueParameter = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = pluginContext.irBuiltIns.unitType,
                branches = parentClass.properties
                    .filter { it.backingField != null }
                    .map { it.name.asString() to it.backingField!!.type }
                    .mapNotNull { (propertyName, type) ->
                        irBranch(
                            condition = irEquals(irGet(propertyNameValueParameter), irString(propertyName)),
                            result = generateDeserializeCall(value = value, type = type) ?: return@mapNotNull null,
                        )
                    }.toList() + irElseBranch(irReturn(irNull())),
            )
        }
    }

    private fun IrBuilderWithScope.generateDeserializeCall(value: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(decodeFromStringFunction).apply {
                extensionReceiver = irCall(json.getter!!)
                putValueArgument(0, irGet(value))
                putTypeArgument(0, if (type.isValueContainer()) {
                    (type as? IrSimpleType)?.arguments?.firstOrNull()?.typeOrNull ?: return null
                } else {
                    type
                })
            }
        )
    }
}
