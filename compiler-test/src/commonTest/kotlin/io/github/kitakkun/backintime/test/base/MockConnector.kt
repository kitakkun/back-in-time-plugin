package io.github.kitakkun.backintime.test.base

import io.github.kitakkun.backintime.runtime.connector.BackInTimeWebSocketConnector
import io.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.awaitCancellation
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
        awaitCancellation()
    }

    override suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent) {
        mutableEventsFromService.add(event)
    }
}
