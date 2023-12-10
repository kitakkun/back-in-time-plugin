package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.MessageCollectorHolder
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.classFqName
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.isNullable
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
    private val valueSetterCallableIds: Set<CallableId>,
) : IrElementTransformerVoid() {
    // reference val backInTimeJson = Json { ... }
    private val json = pluginContext.referenceProperties(BackInTimeConsts.myJsonPropertyId).single().owner
    private val jsonClass = pluginContext.referenceClass(BackInTimeConsts.kotlinxSerializationJsonClassId)!!.owner.declarations.filterIsInstance<IrClass>().first { it.isCompanion && it.name.asString() == "Default" }.symbol
    private val encodeToStringFunction = jsonClass.functions.first { it.owner.name.asString() == "encodeToString" && it.owner.valueParameters.size == 2 }
    private val decodeFromStringFunction = jsonClass.functions.first { it.owner.name.asString() == "decodeFromString" && it.owner.valueParameters.size == 2 }
    private val builtInSerializers = pluginContext.referenceFunctions(CallableId(FqName("kotlinx.serialization.builtins"), Name.identifier("serializer")))

    private val backInTimeRuntimeException = pluginContext.referenceClass(BackInTimeConsts.backInTimeRuntimeExceptionClassId)!!
    private val nullValueNotAssignableExceptionConstructor = backInTimeRuntimeException.owner.sealedSubclasses
        .first { it.owner.classId == BackInTimeConsts.nullValueNotAssignableExceptionClassId }.constructors.first()

    private fun shouldGenerateFunctionBody(parentClass: IrClass): Boolean {
        return parentClass.superTypes.any { it.classFqName == BackInTimeConsts.debuggableStateHolderManipulatorFqName }
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!shouldGenerateFunctionBody(parentClass)) return super.visitSimpleFunction(declaration)

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

        return super.visitFunction(declaration)
    }

    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBlockBody {
        return IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
            val propertyName = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = pluginContext.irBuiltIns.unitType,
                branches = parentClass.properties.map { property ->
                    irBranch(
                        condition = irEquals(irGet(propertyName), irString(property.name.asString())),
                        result = generateSetForProperty(declaration, property, value)
                    )
                }.toList() + irElseBranch(irBlock {}),
            )
        }
    }

    private fun IrBuilderWithScope.generateSetForProperty(declaration: IrFunction, property: IrProperty, value: IrValueParameter): IrExpression {
        val backingField = property.backingField ?: return irBlock { }

        val valueType = property.getValueType()
        if (valueType.classOrNull?.owner?.serializerAvailable() != true) {
            MessageCollectorHolder.reportWarning("property ${property.name} is not serializable")
            return irBlock { }
        }

        val isGeneric = property.backingField?.type?.classOrNull?.owner?.isGeneric() == true

        val irThrowErrorIfNullNotAllowed = irIfNull(
            type = value.type,
            subject = irGet(value),
            thenPart = irThrow(irCallConstructor(nullValueNotAssignableExceptionConstructor, emptyList()).apply {
                putValueArgument(0, irString(property.fqNameWhenAvailable?.asString() ?: "unknown"))
                putValueArgument(1, irString(backingField.type.classFqName?.asString() ?: "unknown"))
            }),
            elsePart = irBlock { },
        )

        return irBlock {
            if (!valueType.isNullable()) +irThrowErrorIfNullNotAllowed

            if (!isGeneric && property.isVar) {
                +irSetField(
                    receiver = irGet(declaration.dispatchReceiverParameter!!),
                    field = backingField,
                    value = irGet(value),
                )
            } else {
                val setterCallableId = valueSetterCallableIds.find { it.classId == backingField.type.classOrNull?.owner?.classId } ?: return irBlock {}
                val propertySetterPattern = Regex("<set-(.*?)>")
                val matchResult = propertySetterPattern.find(setterCallableId.callableName.asString())
                val valueSetter = if (matchResult != null) {
                    backingField.type.classOrNull?.getPropertySetter(matchResult.groupValues[1])
                } else {
                    pluginContext.referenceFunctions(setterCallableId).firstOrNull()
                } ?: return irBlock {}

                +irCall(valueSetter).apply {
                    dispatchReceiver = irGetField(
                        receiver = if (backingField.isStatic) {
                            null
                        } else {
                            irGet(declaration.dispatchReceiverParameter!!)
                        },
                        field = backingField,
                    )
                    putValueArgument(0, irGet(value))
                }
            }
        }
    }

    /**
     * generate body for [DebuggableStateHolderManipulator.serializePropertyForBackInDebug]
     */
    private fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBlockBody {
        return IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
            val propertyName = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = pluginContext.irBuiltIns.unitType,
                branches = parentClass.properties
                    .mapNotNull { property ->
                        val valueType = property.getValueType()
                        if (valueType.classOrNull?.owner?.serializerAvailable() != true) {
                            MessageCollectorHolder.reportWarning("property ${property.name} is not serializable")
                            return@mapNotNull null
                        }
                        irBranch(
                            condition = irEquals(irGet(propertyName), irString(property.name.asString())),
                            result = generateSerializeCall(
                                valueClass = property.getValueType().classOrNull?.owner ?: return@mapNotNull null,
                                value = value,
                            )
                        )
                    }.toList() + irElseBranch(irReturn(irString("NONE"))), // FIXME: should throw an exception
            )
        }
    }

    private fun IrBuilderWithScope.generateSerializeCall(value: IrValueParameter, valueClass: IrClass): IrExpression {
        val getSerializerCall = irGetSerializerCall(valueClass) ?: return irBlock { }

        return irReturn(
            irCall(encodeToStringFunction).apply {
                dispatchReceiver = irCall(json.getter!!)
                putValueArgument(0, getSerializerCall)
                putValueArgument(1, irGet(value))
            }
        )
    }

    private fun IrClass.serializerAvailable(): Boolean {
        if (this.companionObject()?.getSimpleFunction("serializer") != null) return true
        return pluginContext.referenceFunctions(CallableId(FqName("kotlinx.serialization.builtins"), Name.identifier("serializer")))
            .any { it.owner.returnType.getGenericTypes().firstOrNull()?.classOrNull == this.symbol }
    }

    private fun IrClass.isGeneric(): Boolean {
        return this.typeParameters.isNotEmpty()
    }

    /**
     * generate body for [DebuggableStateHolderManipulator.deserializePropertyForBackInDebug]
     */
    private fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass): IrBlockBody {
        return IrBlockBodyBuilder(
            context = pluginContext,
            startOffset = declaration.startOffset,
            endOffset = declaration.endOffset,
            scope = Scope(declaration.symbol),
        ).blockBody {
            val propertyName = declaration.valueParameters[0]
            val value = declaration.valueParameters[1]
            +irWhen(
                type = pluginContext.irBuiltIns.unitType,
                branches = parentClass.properties.mapNotNull { property ->
                    irBranch(
                        condition = irEquals(irGet(propertyName), irString(property.name.asString())),
                        result = generateDeserializeCall(value = value, valueClass = property.getValueType().classOrNull?.owner ?: return@mapNotNull null),
                    )
                }.toList() + irElseBranch(irReturn(irNull())),
            )
        }
    }

    private fun IrBuilderWithScope.generateDeserializeCall(value: IrValueParameter, valueClass: IrClass): IrExpression {
        val getSerializerCall = irGetSerializerCall(valueClass) ?: return irBlock {}

        return irReturn(
            irCall(decodeFromStringFunction).apply {
                dispatchReceiver = irCall(json.getter!!)
                putValueArgument(0, getSerializerCall)
                putValueArgument(1, irGet(value))
            }
        )
    }

    private fun IrBuilderWithScope.irGetSerializerCall(valueClass: IrClass): IrExpression? {
        val builtInSerializer = builtInSerializers.find {
            it.owner.returnType.getGenericTypes().firstOrNull()?.classOrNull == valueClass.symbol
        }

        val companionClass = valueClass.companionObject()
        val companionSerializer = companionClass?.getSimpleFunction("serializer")

        return when {
            builtInSerializer != null ->
                irCall(builtInSerializer).apply {
                    extensionReceiver = irGetObject(builtInSerializer.owner.extensionReceiverParameter!!.type.classOrNull!!)
                }

            companionSerializer != null ->
                irCall(companionSerializer).apply {
                    dispatchReceiver = irGetObject(companionClass.symbol)
                }

            else -> null
        }
    }
}
