package com.github.kitakkun.backintime.websocket.event.model

import kotlinx.serialization.Serializable

@Serializable
data class MethodInfo(
    val name: String,
    val arguments: Map<String, String>,
    val returnType: String,
)
