package com.github.kitakkun.backintime.runtime.event

import kotlinx.serialization.Serializable

@Serializable
data class PropertyInfo(
    val name: String,
    val debuggable: Boolean,
    val isDebuggableStateHolder: Boolean,
    val propertyType: String,
    val valueType: String,
)
