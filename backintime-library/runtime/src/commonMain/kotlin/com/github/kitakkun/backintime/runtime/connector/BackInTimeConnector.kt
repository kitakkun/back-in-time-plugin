package com.github.kitakkun.backintime.runtime.connector

import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow

interface BackInTimeConnector {
    val isConnected: Boolean
    val connectedFlow: Flow<Boolean>
    fun connect()
    fun disconnect()
    fun sendEvent(event: BackInTimeDebugServiceEvent)
    fun receiveEventAsFlow(): Flow<BackInTimeDebuggerEvent>
}
