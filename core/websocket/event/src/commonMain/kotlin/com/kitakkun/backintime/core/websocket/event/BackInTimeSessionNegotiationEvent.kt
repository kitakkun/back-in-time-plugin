package com.kitakkun.backintime.core.websocket.event

import kotlinx.serialization.Serializable

sealed interface BackInTimeSessionNegotiationEvent {
    @Serializable
    data class Request(val sessionId: String?) : BackInTimeSessionNegotiationEvent

    @Serializable
    data class Accept(val sessionId: String) : BackInTimeSessionNegotiationEvent
}
