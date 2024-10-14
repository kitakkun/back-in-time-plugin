package io.github.kitakkun.backintime.compiler.valuecontainer.filter.function

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

enum class Modifier {
    SUSPEND,
    INLINE,
}

/**
 * Filters by [Modifier]
 */
class ModifierFilter(
    private val modifiers: Set<Modifier>,
) : IrFunctionFilter {
    override fun matches(function: IrSimpleFunction): Boolean {
        TODO("Not yet implemented")
    }
}
