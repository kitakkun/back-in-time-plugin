package com.github.kitakkun.backintime.compiler.backend.transformer

import com.github.kitakkun.backintime.compiler.BackInTimeConsts
import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.utils.getSimpleFunctionsRecursively
import com.github.kitakkun.backintime.compiler.backend.utils.irBlockBodyBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
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
                        generateSetForProperty(
                            parentClassReceiver = parentClassReceiver,
                            property = property,
                            valueParameter = valueParameter,
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
                        generateThrowNoSuchPropertyException(
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
                        generateThrowNoSuchPropertyException(
                            parentClassFqName = parentClass.kotlinFqName.asString(),
                            propertyNameParameter = it,
                        )
                    }
                )
            }
        }
    }

    /**
     * generate IrWhen which has branches for each properties like below:
     * when (propertyName) {
     *    "property1" -> { /* do something */ }
     *    "property2" -> { /* do something */ }
     *    else -> { /* do something */ }
     * }
     */
    private fun IrBuilderWithScope.irWhenByProperties(
        properties: List<IrProperty>,
        propertyNameParameter: IrValueParameter,
        buildBranchResultExpression: IrBuilderWithScope.(IrProperty) -> IrExpression?,
        elseBranchExpression: IrBuilderWithScope.(propertyNameParameter: IrValueParameter) -> IrExpression,
    ): IrWhen {
        val branches = properties.mapNotNull { property ->
            val condition = irEquals(irGet(propertyNameParameter), irString(property.name.asString()))
            val result = buildBranchResultExpression(property) ?: return@mapNotNull null
            irBranch(condition = condition, result = result)
        }.plus(irElseBranch(irBlock { +elseBranchExpression(propertyNameParameter) }))

        return irWhen(
            type = pluginContext.irBuiltIns.unitType,
            branches = branches,
        )
    }

    context(IrBuilderWithScope)
    private fun generateSetForProperty(
        parentClassReceiver: IrValueParameter,
        property: IrProperty,
        valueParameter: IrValueParameter,
    ): IrExpression? {
        val propertyType = property.getter?.returnType ?: return null

        return when {
            propertyType.isValueContainer() -> generateValueContainerSetterCall(parentClassReceiver, property, valueParameter)

            property.isVar -> {
                val setter = property.setter ?: return null
                irCall(setter).apply {
                    dispatchReceiver = irGet(parentClassReceiver)
                    putValueArgument(0, irGet(valueParameter))
                }
            }

            else -> null
        }
    }

    private fun IrBuilderWithScope.generateValueContainerSetterCall(
        parentClassReceiver: IrValueParameter,
        property: IrProperty,
        valueParameter: IrValueParameter,
    ): IrExpression? {
        val propertyGetter = property.getter ?: return null
        val propertyClass = property.getter?.returnType?.classOrNull?.owner ?: return null
        val valueContainerClassInfo = valueContainerClassInfoList.find { it.classId == propertyClass.classId } ?: return null

        val propertyGetterCall = irCall(propertyGetter).apply {
            dispatchReceiver = irGet(parentClassReceiver)
        }

        val preSetterCalls = valueContainerClassInfo.preSetterFunctionNames
            .map { propertyClass.getSimpleFunctionsRecursively(it).firstOrNull { it.owner.valueParameters.isEmpty() } }
            .map {
                // 一部関数のシンボルが欠落している場合は処理継続不可
                if (it == null) return null
                irCall(it).apply { dispatchReceiver = propertyGetterCall }
            }

        val valueSetterCall = propertyClass
            .getSimpleFunctionsRecursively(valueContainerClassInfo.setterFunctionName)
            .firstOrNull { it.owner.valueParameters.size == 1 }
            ?.let {
                irCall(it).apply {
                    dispatchReceiver = propertyGetterCall
                    putValueArgument(0, irGet(valueParameter))
                }
            } ?: return null

        return irComposite {
            +preSetterCalls
            +valueSetterCall
        }
    }

    private fun IrType.isValueContainer(): Boolean {
        return valueContainerClassInfoList.any { it.classId == this.classOrNull?.owner?.classId }
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
