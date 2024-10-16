package io.github.kitakkun.backintime.core.runtime.connector

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow

interface BackInTimeWebSocketConnector {
    suspend fun connect(): Flow<BackInTimeDebuggerEvent>
    suspend fun awaitCloseSession()
    suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent)
    suspend fun close()
}
