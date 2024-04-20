package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irBlock
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irComposite
import org.jetbrains.kotlin.ir.builders.irElseBranch
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrSymbolOwner
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrWhen
import org.jetbrains.kotlin.ir.types.classOrNull

fun IrSymbolOwner.irBlockBuilder(pluginContext: IrPluginContext) = IrBlockBuilder(
    context = pluginContext,
    scope = Scope(symbol),
    startOffset = startOffset,
    endOffset = endOffset,
)

fun IrSymbolOwner.irBlockBodyBuilder(pluginContext: IrPluginContext) = IrBlockBodyBuilder(
    context = pluginContext,
    scope = Scope(symbol),
    startOffset = startOffset,
    endOffset = endOffset,
)

fun IrExpression.irBlockBuilder(pluginContext: IrPluginContext, scope: Scope) = IrBlockBuilder(
    context = pluginContext,
    scope = scope,
    startOffset = startOffset,
    endOffset = endOffset,
)

fun IrExpression.irBlockBodyBuilder(pluginContext: IrPluginContext, scope: Scope) = IrBlockBodyBuilder(
    context = pluginContext,
    scope = scope,
    startOffset = startOffset,
    endOffset = endOffset,
)

context(BackInTimePluginContext)
fun IrSymbolOwner.irBlockBuilder() = irBlockBuilder(pluginContext)

context(BackInTimePluginContext)
fun IrSymbolOwner.irBlockBodyBuilder() = irBlockBodyBuilder(pluginContext)

context(BackInTimePluginContext)
fun IrExpression.irBlockBuilder(scope: Scope) = irBlockBuilder(pluginContext, scope)

context(BackInTimePluginContext)
fun IrExpression.irBlockBodyBuilder(scope: Scope) = irBlockBodyBuilder(pluginContext, scope)

context(IrPluginContext)
fun IrBuilderWithScope.irPropertySetterCall(
    propertySetter: IrSimpleFunction,
    dispatchReceiverParameter: IrValueParameter,
    valueParameter: IrValueParameter,
) = irCall(propertySetter).apply {
    this.dispatchReceiver = irGet(dispatchReceiverParameter)
    putValueArgument(0, irGet(valueParameter))
}

fun IrBuilderWithScope.irPropertyGetterCall(
    propertyGetter: IrSimpleFunction,
    dispatchReceiverParameter: IrValueParameter,
) = irCall(propertyGetter).apply {
    this.dispatchReceiver = irGet(dispatchReceiverParameter)
}

context(IrPluginContext)
fun IrBuilderWithScope.irValueContainerPropertySetterCall(
    propertyGetter: IrSimpleFunction,
    dispatchReceiverParameter: IrValueParameter,
    valueParameter: IrValueParameter,
    valueContainerClassInfo: ValueContainerClassInfo,
): IrExpression? {
    val propertyGetterCall = irPropertyGetterCall(propertyGetter, dispatchReceiverParameter)

    val klass = propertyGetter.returnType.classOrNull?.owner ?: return null
    val preSetterCallSymbols = valueContainerClassInfo.preSetterFunctionNames
        .mapNotNull { klass.getSimpleFunctionsRecursively(it).firstOrNull { it.owner.valueParameters.isEmpty() } }
    val setterCallSymbol = klass.getSimpleFunctionsRecursively(valueContainerClassInfo.setterFunctionName)
        .firstOrNull { it.owner.valueParameters.size == 1 } ?: return null

    // 一部関数のシンボルが欠落している場合は処理継続不可
    if (preSetterCallSymbols.size != valueContainerClassInfo.preSetterFunctionNames.size) return null

    val preSetterCalls = preSetterCallSymbols.map { irCall(it).apply { dispatchReceiver = propertyGetterCall } }
    val setterCall = irCall(setterCallSymbol).apply {
        dispatchReceiver = propertyGetterCall
        putValueArgument(0, irGet(valueParameter))
    }

    return irComposite {
        +preSetterCalls
        +setterCall
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
context(IrPluginContext)
fun IrBuilderWithScope.irWhenByProperties(
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
        type = irBuiltIns.unitType,
        branches = branches,
    )
}
