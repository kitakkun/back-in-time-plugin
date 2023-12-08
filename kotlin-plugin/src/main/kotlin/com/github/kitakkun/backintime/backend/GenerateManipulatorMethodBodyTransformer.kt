package com.github.kitakkun.backintime.backend

import com.github.kitakkun.backintime.BackInTimeConsts
import com.github.kitakkun.backintime.MessageCollectorHolder
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
    private val valueSetterCallableIds: List<CallableId>,
) : IrElementTransformerVoid() {
    private val jsonClass = pluginContext.referenceClass(BackInTimeConsts.kotlinxSerializationJsonClassId)!!.owner.declarations.filterIsInstance<IrClass>().first { it.isCompanion && it.name.asString() == "Default" }.symbol
    private val encodeToStringFunction = jsonClass.functions.first { it.owner.name.asString() == "encodeToString" && it.owner.valueParameters.size == 2 }

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
            BackInTimeConsts.forceSetPropertyValueForBackInDebugMethodName -> {
                declaration.body = generateForceSetPropertyMethodBody(declaration, parentClass)
            }

            BackInTimeConsts.serializePropertyMethodName -> {
                declaration.body = generateSerializePropertyMethodBody(declaration, parentClass)
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

    private fun IrBlockBodyBuilder.generateSetForProperty(declaration: IrFunction, property: IrProperty, value: IrValueParameter): IrExpression {
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
            thenPart = irBlock {
                irThrow(irCallConstructor(nullValueNotAssignableExceptionConstructor, emptyList()).apply {
                    putValueArgument(0, irString(property.fqNameWhenAvailable?.asString() ?: "unknown"))
                    putValueArgument(1, irString(backingField.type.classFqName?.asString() ?: "unknown"))
                })
            },
            elsePart = irBlock { },
        )

        return irBlock {
            if (valueType.isNullable()) +irThrowErrorIfNullNotAllowed

            if (!isGeneric && property.isVar) {
                irSetField(
                    receiver = irGet(declaration.dispatchReceiverParameter!!),
                    field = backingField,
                    value = irGet(value),
                )
            } else {
                val setterCallableId = valueSetterCallableIds.find { it.className == valueType.classOrNull?.owner?.fqNameWhenAvailable } ?: return irBlock {}
                val propertySetterPattern = Regex("<set-(.*?)>")
                val matchResult = propertySetterPattern.find(setterCallableId.callableName.asString())
                val valueSetter = if (matchResult != null) {
                    property.backingField?.type?.classOrNull?.getPropertySetter(matchResult.groupValues[1])
                } else {
                    pluginContext.referenceFunctions(setterCallableId).firstOrNull()
                } ?: return irBlock {}

                irCall(valueSetter).apply {
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
                    .filter { it.backingField?.type?.classOrNull?.owner?.serializerAvailable() == true }
                    .map { property ->
                        irBranch(
                            condition = irEquals(irGet(propertyName), irString(property.name.asString())),
                            result = generateSerializeCall(
                                valueClass = property.backingField!!.type.classOrNull!!.owner,
                                value = value,
                            )
                        )
                    }.toList() + irElseBranch(irReturn(irString("NONE"))), // FIXME: should throw an exception
            )
        }
    }

    private fun IrBuilderWithScope.generateSerializeCall(value: IrValueParameter, valueClass: IrClass): IrExpression {
        val companionClass = valueClass.companionObject()

        // String型などはCompanionObjectがないので，builtinのserializer拡張関数を探す
        val serializer = valueClass.companionObject()?.getSimpleFunction("serializer")
            ?: pluginContext.referenceFunctions(CallableId(FqName("kotlinx.serialization.builtins"), Name.identifier("serializer")))
                .find { it.owner.returnType.getGenericTypes().firstOrNull()?.classOrNull == valueClass.symbol }
            ?: error("serializer not found for ${valueClass.fqNameWhenAvailable}")

        val serializerCall = irCall(serializer).apply {
            if (companionClass == null) {
                extensionReceiver = irGetObject(serializer.owner.extensionReceiverParameter!!.type.classOrNull!!)
            } else {
                dispatchReceiver = irGetObject(companionClass.symbol)
            }
        }

        return irReturn(
            irCall(encodeToStringFunction).apply {
                dispatchReceiver = irGetObject(jsonClass)
                putValueArgument(0, serializerCall)
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
}
