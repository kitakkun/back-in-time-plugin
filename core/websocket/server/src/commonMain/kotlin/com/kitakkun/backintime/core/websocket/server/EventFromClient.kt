package com.kitakkun.backintime.core.websocket.server

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent

data class EventFromClient(
    val sessionId: String,
    val event: BackInTimeDebugServiceEvent,
)
