package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertyName
import com.github.kitakkun.backintime.compiler.backend.utils.getPropertySetterRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import com.github.kitakkun.backintime.compiler.backend.utils.isSetterName
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.name.Name

/**
 * generate DebuggableStateHolderManipulator methods bodies
 */
context(BackInTimePluginContext)
class ManipulatorMethodBodyGenerationTransformer : IrElementTransformerVoid() {
    private fun shouldGenerateFunctionBody(parentClass: IrClass) = parentClass.superTypes.contains(manipulatorClassType)

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return super.visitSimpleFunction(declaration)
        if (!shouldGenerateFunctionBody(parentClass)) return super.visitSimpleFunction(declaration)

        with(declaration.irBlockBodyBuilder()) {
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
                generateSetForProperty(
                    parentClassReceiver = parentClassReceiver,
                    property = property,
                    value = value,
                )
            },
            elseBranchExpression = {
                generateThrowNoSuchPropertyException(
                    parentClassFqName = parentClass.kotlinFqName.asString(),
                    propertyNameParameter = it,
                )
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
            elseBranchExpression = {
                generateThrowNoSuchPropertyException(
                    parentClassFqName = parentClass.kotlinFqName.asString(),
                    propertyNameParameter = it,
                )
            }
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
            elseBranchExpression = {
                generateThrowNoSuchPropertyException(
                    parentClassFqName = parentClass.kotlinFqName.asString(),
                    propertyNameParameter = it,
                )
            }
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
        elseBranchExpression: IrBuilderWithScope.(propertyNameParameter: IrValueParameter) -> IrExpression,
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
            }.toList() + irElseBranch(irBlock { +elseBranchExpression(propertyNameParameter) })
    )

    context(IrBuilderWithScope)
    private fun generateSetForProperty(
        parentClassReceiver: IrValueParameter,
        property: IrProperty,
        value: IrValueParameter,
    ): IrExpression? {
        val propertyType = property.getter?.returnType ?: return null
        if (!propertyType.isValueContainer() && property.isVar) {
            val setter = property.setter ?: return null
            return irCall(setter).apply {
                dispatchReceiver = irGet(parentClassReceiver)
                putValueArgument(0, irGet(value))
            }
        }

        val propertyClass = property.getter?.returnType?.classOrNull?.owner ?: return null
        val setterCallableName = valueContainerClassInfoList.find { it.classId == propertyClass.classId }?.valueSetter?.callableName ?: return null
        val valueSetter = when {
            setterCallableName.isSetterName() -> propertyClass.getPropertySetterRecursively(setterCallableName.getPropertyName())
            else -> propertyClass.getSimpleFunctionRecursively(setterCallableName.asString())
        }

        val propertyGetter = property.getter ?: return null
        if (valueSetter != null) {
            return irCall(valueSetter).apply {
                dispatchReceiver = irCall(propertyGetter).apply {
                    dispatchReceiver = irGet(parentClassReceiver)
                }
                putValueArgument(0, irGet(value))
            }
        } else if (setterCallableName.isCombinedCallsName()) {
            // FIXME: もうちょっとうまく書きたい
            val sequence = setterCallableName.asString().split(",").map { Name.guessByFirstCharacter(it) }
            return irComposite {
                +sequence.dropLast(1).mapNotNull { name ->
                    val function = when {
                        name.isSetterName() -> propertyClass.getPropertySetterRecursively(name.getPropertyName())
                        else -> propertyClass.getSimpleFunctionRecursively(name.asString())
                    } ?: return@mapNotNull null

                    irCall(function).apply {
                        dispatchReceiver = irCall(propertyGetter).apply {
                            dispatchReceiver = irGet(parentClassReceiver)
                        }
                    }
                }
                val lastFunctionName = sequence.last()
                val lastFunction = when {
                    lastFunctionName.isSetterName() -> propertyClass.getPropertySetterRecursively(lastFunctionName.getPropertyName())
                    else -> propertyClass.getSimpleFunctionRecursively(lastFunctionName.asString())
                } ?: return@irComposite

                irCall(lastFunction).apply {
                    dispatchReceiver = irCall(propertyGetter).apply {
                        dispatchReceiver = irGet(parentClassReceiver)
                    }
                    putValueArgument(0, irGet(value))
                }
            }
        }
        return null
    }

    private fun Name.isCombinedCallsName(): Boolean {
        return this.asString().split(",").size > 1
    }

    private fun IrType.isValueContainer(): Boolean {
        return valueContainerClassInfoList.any { it.classId == this.classOrNull?.owner?.classId }
    }

    private fun IrType.isSerializableItSelf(): Boolean {
        return valueContainerClassInfoList.any { it.classId == this.classOrNull?.owner?.classId && it.serializeItSelf }
    }

    private fun IrBuilderWithScope.generateSerializeCall(value: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(encodeToStringFunction).apply {
                extensionReceiver = irCall(backInTimeJsonGetter)
                putValueArgument(0, irGet(value))
                putTypeArgument(
                    index = 0,
                    type = when {
                        type.isSerializableItSelf() -> type
                        type.isValueContainer() -> {
                            (type as? IrSimpleType)?.arguments?.firstOrNull()?.typeOrNull ?: return null
                        }

                        else -> type
                    }
                )
            }
        )
    }

    private fun IrBuilderWithScope.generateDeserializeCall(value: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(decodeFromStringFunction).apply {
                extensionReceiver = irCall(backInTimeJsonGetter)
                putValueArgument(0, irGet(value))
                putTypeArgument(
                    index = 0,
                    type = when {
                        type.isValueContainer() -> {
                            if (type.isSerializableItSelf()) type
                            else (type as? IrSimpleType)?.arguments?.firstOrNull()?.typeOrNull ?: return null
                        }

                        else -> type
                    }
                )
            }
        )
    }

    private fun IrBuilderWithScope.generateThrowNoSuchPropertyException(
        parentClassFqName: String,
        propertyNameParameter: IrValueParameter,
    ) = irThrow(
        irCallConstructor(noSuchPropertyExceptionConstructor, emptyList()).apply {
            putValueArgument(0, irString(parentClassFqName))
            putValueArgument(1, irGet(propertyNameParameter))
        }
    )
}
