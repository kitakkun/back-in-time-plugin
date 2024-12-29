package com.kitakkun.backintime.core.websocket.server

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent

data class EventToClient(
    val sessionId: String,
    val event: BackInTimeDebuggerEvent,
)
