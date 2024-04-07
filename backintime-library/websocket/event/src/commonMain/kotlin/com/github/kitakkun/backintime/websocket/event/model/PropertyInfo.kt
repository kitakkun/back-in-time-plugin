package com.github.kitakkun.backintime.websocket.event.model

import kotlinx.serialization.Serializable

@Serializable
data class PropertyInfo(
    val name: String,
    val type: String,
    val valueType: String,
    val isNullable: Boolean,
)
