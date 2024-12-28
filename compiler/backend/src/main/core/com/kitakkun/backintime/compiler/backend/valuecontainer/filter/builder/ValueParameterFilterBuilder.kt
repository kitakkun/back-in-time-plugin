package com.kitakkun.backintime.compiler.backend.valuecontainer.filter.builder

import com.kitakkun.backintime.compiler.backend.valuecontainer.filter.function.ValueParameterFilter
import com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type.GenericTypeMatcher
import com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type.TypeMatcher
import com.kitakkun.backintime.compiler.backend.valuecontainer.filter.type.TypeParameterMatcher
import org.jetbrains.kotlin.name.ClassId

class ValueParameterFilterBuilder {
    private val typeMatchers = mutableListOf<TypeMatcher>()

    fun addTypeMatcher(matcher: TypeMatcher) {
        typeMatchers += matcher
    }

    fun typeParameter(index: Int): TypeParameterMatcher {
        return TypeParameterMatcher(index)
    }

    fun genericParameter(classId: ClassId, vararg args: TypeMatcher): GenericTypeMatcher {
        return GenericTypeMatcher(
            classId = classId,
            args = args.toList(),
        )
    }

    fun build(): ValueParameterFilter {
        return ValueParameterFilter(
            typeMatchers = typeMatchers,
        )
    }
}
