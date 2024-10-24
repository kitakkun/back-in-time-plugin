package io.github.kitakkun.backintime.compiler.valuecontainer.filter.function

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.name.Name

/**
 * Filters by [Name].
 */
class NameFilter(val name: Name) : IrFunctionFilter {
    override fun matches(function: IrSimpleFunction): Boolean {
        return function.name == name
    }
}
