package com.kitakkun.backintime.compiler.yaml

import kotlinx.serialization.Serializable

@Serializable
data class StateAccessor(
    val getter: CallableSignature,
    val setter: CallableSignature,
    val preSetter: CallableSignature? = null,
)
