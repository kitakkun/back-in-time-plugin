package com.github.kitakkun.backintime.runtime.connector

import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow

interface BackInTimeWebSocketConnector {
    suspend fun connect(): Flow<BackInTimeDebuggerEvent>
    suspend fun awaitCloseSession()
    suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent)
    suspend fun close()
}
