package io.github.kitakkun.backintime.compiler.valuecontainer.filter.function

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

/**
 * a filter for [IrSimpleFunction].
 */
sealed interface IrFunctionFilter {
    fun matches(function: IrSimpleFunction): Boolean
}
