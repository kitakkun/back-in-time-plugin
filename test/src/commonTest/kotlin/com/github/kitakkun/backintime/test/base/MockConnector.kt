package com.github.kitakkun.backintime.test.base

import com.github.kitakkun.backintime.runtime.connector.BackInTimeWebSocketConnector
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class MockConnector : BackInTimeWebSocketConnector {
    private val mutableEventsFromService = mutableListOf<BackInTimeDebugServiceEvent>()
    val eventsFromService get() = mutableEventsFromService.toList()

    private val mutableEventFromDebuggerFlow = MutableSharedFlow<BackInTimeDebuggerEvent>()

    override suspend fun connect(): Flow<BackInTimeDebuggerEvent> = mutableEventFromDebuggerFlow

    override suspend fun close() {
        // no-op
    }

    override suspend fun awaitCloseSession() {
        // no-op
    }

    override suspend fun sendOrQueueEvent(event: BackInTimeDebugServiceEvent) {
        mutableEventsFromService.add(event)
    }
}
