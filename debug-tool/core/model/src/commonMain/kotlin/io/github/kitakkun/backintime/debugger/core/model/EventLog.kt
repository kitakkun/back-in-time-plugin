package io.github.kitakkun.backintime.debugger.core.model

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent

data class EventLog(
    val id: String,
    val payload: BackInTimeDebugServiceEvent,
    val createdAt: Long,
)
