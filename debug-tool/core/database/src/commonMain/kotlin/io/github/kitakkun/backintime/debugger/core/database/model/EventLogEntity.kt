package io.github.kitakkun.backintime.debugger.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent

@Entity(tableName = "event_log")
data class EventLogEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val payload: BackInTimeDebugServiceEvent,
    val createdAt: Long,
)
