package com.kitakkun.backintime.core.websocket.event.model

import kotlinx.serialization.Serializable

@Serializable
data class PropertyInfo(
    val name: String,
    val debuggable: Boolean,
    val isDebuggableStateHolder: Boolean,
    val propertyType: String,
    val valueType: String,
)
