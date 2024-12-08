package com.kitakkun.backintime.compiler.backend.valuecontainer.filter.function

import com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type.TypeMatcher
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction

class ValueParameterFilter(private val typeMatchers: List<TypeMatcher>) : IrFunctionFilter {
    override fun matches(function: IrSimpleFunction): Boolean {
        val valueParameters = function.valueParameters
        if (typeMatchers.size != valueParameters.size) return false

        return valueParameters.zip(typeMatchers).all { (valueParameter, typeMatcher) ->
            typeMatcher.matches(valueParameter.type)
        }
    }
}
