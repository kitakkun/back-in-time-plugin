package com.github.kitakkun.backintime.websocket.event

import kotlinx.serialization.Serializable

@Serializable
sealed interface DebuggerToAppEvent {
    @Serializable
    data object Ping : DebuggerToAppEvent

    @Serializable
    data class ForceSetProperty(
        val propertyName: String,
        val jsonValue: String,
    ) : DebuggerToAppEvent

    @Serializable
    data class RequestGetInstanceState(
        val instanceIds: List<String>,
    ) : DebuggerToAppEvent
}
