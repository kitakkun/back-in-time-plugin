package com.github.kitakkun.backintime.compiler.backend.utils

import com.github.kitakkun.backintime.compiler.backend.BackInTimePluginContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrSymbolOwner
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.impl.IrErrorClassImpl.symbol

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

fun IrExpression.irBlockBuilder(pluginContext: IrPluginContext) = IrBlockBuilder(
    context = pluginContext,
    scope = Scope(symbol),
    startOffset = startOffset,
    endOffset = endOffset,
)

fun IrExpression.irBlockBodyBuilder(pluginContext: IrPluginContext) = IrBlockBodyBuilder(
    context = pluginContext,
    scope = Scope(symbol),
    startOffset = startOffset,
    endOffset = endOffset,
)

context(BackInTimePluginContext)
fun IrSymbolOwner.irBlockBuilder() = irBlockBuilder(pluginContext)

context(BackInTimePluginContext)
fun IrSymbolOwner.irBlockBodyBuilder() = irBlockBodyBuilder(pluginContext)

context(BackInTimePluginContext)
fun IrExpression.irBlockBuilder() = irBlockBuilder(pluginContext)

context(BackInTimePluginContext)
fun IrExpression.irBlockBodyBuilder() = irBlockBodyBuilder(pluginContext)
