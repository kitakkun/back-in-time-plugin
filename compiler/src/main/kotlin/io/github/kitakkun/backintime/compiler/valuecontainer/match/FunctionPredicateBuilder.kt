package io.github.kitakkun.backintime.compiler.valuecontainer.match

import io.github.kitakkun.backintime.compiler.valuecontainer.filter.builder.ValueParameterFilterBuilder
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.function.ExtensionReceiverFilter
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.function.IrFunctionFilter
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.function.NameFilter
import io.github.kitakkun.backintime.compiler.valuecontainer.filter.type.TypeMatcher
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

class FunctionPredicateBuilder private constructor(private val isLookupPredicate: Boolean) {
    private lateinit var callableId: CallableId
    private val filters = mutableListOf<IrFunctionFilter>()

    constructor(callableId: CallableId) : this(true) {
        this.callableId = callableId
    }

    constructor(name: String) : this(false) {
        filters += NameFilter(Name.guessByFirstCharacter(name))
    }

    fun build(): FunctionPredicate {
        return if (isLookupPredicate) {
            LookupPredicate(callableId, filters)
        } else {
            MemberDeclarationPredicate(filters)
        }
    }

    fun valueParameters(block: ValueParameterFilterBuilder.() -> Unit) {
        filters += ValueParameterFilterBuilder().apply(block).build()
    }

    fun emptyValueParameters() {
        filters += ValueParameterFilterBuilder().build()
    }

    fun extensionReceiver(typeMatcher: TypeMatcher) {
        filters += ExtensionReceiverFilter(typeMatcher)
    }
}

fun memberFunction(name: String, block: FunctionPredicateBuilder.() -> Unit = {}): FunctionPredicate {
    return FunctionPredicateBuilder(name)
        .apply(block)
        .build()
}

fun memberPropertyGetter(propertyName: String, block: FunctionPredicateBuilder.() -> Unit = {}): FunctionPredicate {
    return FunctionPredicateBuilder("<get-$propertyName>")
        .apply(block)
        .build()
}

fun memberPropertySetter(propertyName: String, block: FunctionPredicateBuilder.() -> Unit = {}): FunctionPredicate {
    return FunctionPredicateBuilder("<set-$propertyName>")
        .apply(block)
        .build()
}

fun function(callableId: CallableId, block: FunctionPredicateBuilder.() -> Unit = {}): FunctionPredicate {
    return FunctionPredicateBuilder(callableId)
        .apply(block)
        .build()
}
