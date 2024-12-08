package com.kitakkun.backintime.core.runtime.connector

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow

interface BackInTimeWebSocketConnector {
    suspend fun connect(): Flow<BackInTimeDebuggerEvent>
    suspend fun awaitCloseSession()
    suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent)
    suspend fun close()
}
