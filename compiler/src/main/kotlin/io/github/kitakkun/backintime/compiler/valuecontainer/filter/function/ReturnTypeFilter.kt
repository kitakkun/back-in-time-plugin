package io.github.kitakkun.backintime.compiler.valuecontainer.filter.function

import io.github.kitakkun.backintime.compiler.valuecontainer.filter.type.TypeMatcher
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

class ReturnTypeFilter(private val typeMatcher: TypeMatcher) : IrFunctionFilter {
    override fun matches(function: IrSimpleFunction): Boolean {
        return typeMatcher.matches(function.returnType)
    }
}
