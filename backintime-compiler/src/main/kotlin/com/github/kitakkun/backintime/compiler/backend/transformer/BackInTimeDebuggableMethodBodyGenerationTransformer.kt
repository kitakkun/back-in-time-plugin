package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionsRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.psi.stubs.impl.serialize

/**
 * generate BackInTimeDebuggable methods bodies
 */
context(BackInTimePluginContext)
class BackInTimeDebuggableMethodBodyGenerationTransformer : IrElementTransformerVoid() {
    private fun shouldGenerateFunctionBody(parentClass: IrClass) = parentClass.superTypes.contains(backInTimeDebuggableInterfaceType)

    override fun visitSimpleFunction(declaration: IrSimpleFunction): IrStatement {
        val parentClass = declaration.parentClassOrNull ?: return declaration
        if (!shouldGenerateFunctionBody(parentClass)) return declaration

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
     * generate body for [BackInTimeDebuggable.forceSetValue]
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
     * generate body for [BackInTimeDebuggable.serialize]
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
     * generate body for [BackInTimeDebuggable.deserialize]
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

        when {
            propertyType.isValueContainer() -> {
                val propertyGetter = property.getter ?: return null
                val propertyClass = propertyType.classOrNull?.owner ?: return null
                val valueContainerClassInfo = valueContainerClassInfoList.find { it.classId == propertyClass.classId } ?: return null

                val preSetterCalls = valueContainerClassInfo.preSetterFunctionNames
                    .map { propertyClass.getSimpleFunctionsRecursively(it).firstOrNull { it.owner.valueParameters.isEmpty() } }
                    .map {
                        if (it == null) return null
                        irCall(it).apply {
                            dispatchReceiver = irCall(propertyGetter).apply {
                                dispatchReceiver = irGet(parentClassReceiver)
                            }
                        }
                    }

                val setterCall = propertyClass.getSimpleFunctionsRecursively(valueContainerClassInfo.setterFunctionName)
                    .firstOrNull { it.owner.valueParameters.size == 1 }?.let {
                        irCall(it).apply {
                            dispatchReceiver = irCall(propertyGetter).apply {
                                dispatchReceiver = irGet(parentClassReceiver)
                            }
                            putValueArgument(0, irGet(value))
                        }
                    } ?: return null

                return irComposite {
                    +preSetterCalls
                    +setterCall
                }
            }

            property.isVar -> {
                val setter = property.setter ?: return null
                return irCall(setter).apply {
                    dispatchReceiver = irGet(parentClassReceiver)
                    putValueArgument(0, irGet(value))
                }
            }

            else -> return null
        }
    }

    private fun IrType.isValueContainer(): Boolean {
        return valueContainerClassInfoList.any { it.classId == this.classOrNull?.owner?.classId }
    }

    private fun IrBuilderWithScope.generateSerializeCall(value: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(encodeToStringFunction).apply {
                extensionReceiver = irCall(backInTimeJsonGetter)
                putValueArgument(0, irGet(value))
                putTypeArgument(index = 0, type = type.getSerializerType() ?: return null)
            }
        )
    }

    private fun IrBuilderWithScope.generateDeserializeCall(value: IrValueParameter, type: IrType): IrExpression? {
        return irReturn(
            irCall(decodeFromStringFunction).apply {
                extensionReceiver = irCall(backInTimeJsonGetter)
                putValueArgument(0, irGet(value))
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
