package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.Serializable

@Serializable
sealed interface ParametersSignature {
    @Serializable
    data object Any : ParametersSignature

    @Serializable
    data class Specified(val parameterTypes: List<TypeSignature>) : ParametersSignature
}