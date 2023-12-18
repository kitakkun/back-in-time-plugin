package com.github.kitakkun.backintime.compiler.backend

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isSetterName
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.ir.isReifiable
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid

/**
 * generate DebuggableStateHolderManipulator methods bodies
 */
class GenerateManipulatorMethodBodyTransformer(
    private val pluginContext: IrPluginContext,
    private val valueContainerClassInfoList: List<ValueContainerClassInfo>,
) : IrElementTransformerVoid() {
    private val json = pluginContext.referenceProperties(BackInTimeConsts.backInTimeJsonCallableId).single().owner
    private val encodeToStringFunction = pluginContext.referenceFunctions(BackInTimeConsts.kotlinxSerializationEncodeToStringCallableId).first {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    }
    private val decodeFromStringFunction = pluginContext.referenceFunctions(BackInTimeConsts.kotlinxSerializationDecodeFromStringCallableId).first {
        it.owner.isReifiable() && it.owner.typeParameters.size == 1 && it.owner.valueParameters.size == 1
    }

    private val backInTimeRuntimeException = pluginContext.referenceClass(BackInTimeConsts.backInTimeRuntimeExceptionClassId)!!
    private val nullValueNotAssignableExceptionConstructor = backInTimeRuntimeException.owner.sealedSubclasses
        .first { it.owner.classId == BackInTimeConsts.nullValueNotAssignableExceptionClassId }.constructors.first()

    private val manipulatorClassType = pluginContext.referenceClass(BackInTimeConsts.debuggableStateHolderManipulatorClassId)!!.defaultType

    private fun shouldGenerateFunctionBody(parentClass: IrClass) = parentClass.superTypes.contains(manipulatorClassType)

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!shouldGenerateFunctionBody(parentClass)) return super.visitSimpleFunction(declaration)

        with(declaration.irBlockBodyBuilder(pluginContext)) {
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

    /**
     * generate body for [DebuggableStateHolderManipulator.forceSetValue]
     */
    context(IrBuilderWithScope)
    private fun generateForceSetPropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass) = irBlockBody {
        val parentClassReceiver = declaration.dispatchReceiverParameter ?: return@irBlockBody
        val propertyNameParameter = declaration.valueParameters[0]
        val value = declaration.valueParameters[1]

        +parentClass.irWhenByProperties(
            propertyNameParameter = propertyNameParameter,
            buildBranchResultExpression = { property ->
                val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                generateSetForProperty(
                    parentClassReceiver = parentClassReceiver,
                    property = property,
                    type = propertyType,
                    value = value,
                )
            },
            elseBranchExpression = {
                // TODO: throw an exception
                irBlock { }
            }
        )
    }

    /**
     * generate body for [DebuggableStateHolderManipulator.serialize]
     */
    context(IrBuilderWithScope)
    private fun generateSerializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass) = irBlockBody {
        val propertyNameValueParameter = declaration.valueParameters[0]
        val value = declaration.valueParameters[1]
        +parentClass.irWhenByProperties(
            propertyNameParameter = propertyNameValueParameter,
            buildBranchResultExpression = { property ->
                val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                generateSerializeCall(type = propertyType, value = value)
            },
            elseBranchExpression = { irReturn(irString("NONE")) } // FIXME: should throw an exception
        )
    }

    /**
     * generate body for [DebuggableStateHolderManipulator.deserialize]
     */
    context(IrBuilderWithScope)
    private fun generateDeserializePropertyMethodBody(declaration: IrSimpleFunction, parentClass: IrClass) = irBlockBody {
        val propertyNameValueParameter = declaration.valueParameters[0]
        val value = declaration.valueParameters[1]
        +parentClass.irWhenByProperties(
            propertyNameParameter = propertyNameValueParameter,
            buildBranchResultExpression = { property ->
                val propertyType = property.getter?.returnType ?: return@irWhenByProperties null
                generateDeserializeCall(value = value, type = propertyType)
            },
            elseBranchExpression = { irReturn(irNull()) }
        )
    }

    /**
     * generate IrWhen which has branches for each properties like below:
     * when (propertyName) {
     *    "property1" -> { /* do something */ }
     *    "property2" -> { /* do something */ }
     *    else -> { /* do something */ }
     * }
     */
    context(IrBuilderWithScope)
    private fun IrClass.irWhenByProperties(
        propertyNameParameter: IrValueParameter,
        buildBranchResultExpression: IrBuilderWithScope.(IrProperty) -> IrExpression?,
        elseBranchExpression: IrBuilderWithScope.() -> IrExpression,
    ) = irWhen(
        type = pluginContext.irBuiltIns.unitType,
        branches = properties
            .mapNotNull { property ->
                irBranch(
                    condition = irEquals(irGet(propertyNameParameter), irString(property.name.asString())),
                    result = irBlock {
                        val innerExpression = buildBranchResultExpression(property) ?: return@mapNotNull null
                        +innerExpression
                    },
                )
            }.toList() + irElseBranch(irBlock { +elseBranchExpression() })
    )


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
