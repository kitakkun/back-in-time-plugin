package io.github.kitakkun.backintime.compiler.valuecontainer.filter.builder

import io.github.kitakkun.backintime.compiler.valuecontainer.filter.function.ValueParameterFilter
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.type.GenericTypeMatcher
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.type.NormalTypeMatcher
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.type.TypeMatcher
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.type.TypeParameterMatcher
import org.jetbrains.kotlin.name.ClassId

class ValueParameterFilterBuilder {
    private val typeMatchers = mutableListOf<TypeMatcher>()

    fun addTypeMatcher(matcher: TypeMatcher) {
        typeMatchers += matcher
    }

    fun normalParameter(classId: ClassId): NormalTypeMatcher {
        return NormalTypeMatcher(classId = classId)
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
