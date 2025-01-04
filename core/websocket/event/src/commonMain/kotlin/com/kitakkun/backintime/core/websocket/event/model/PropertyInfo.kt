package com.kitakkun.backintime.core.websocket.event.model

import kotlinx.serialization.Serializable

@Serializable
data class PropertyInfo(
    val signature: String,
    val debuggable: Boolean,
    val isDebuggableStateHolder: Boolean,
    val propertyType: String,
    val valueType: String,
)
