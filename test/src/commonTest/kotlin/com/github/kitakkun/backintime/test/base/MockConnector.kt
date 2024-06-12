package com.github.kitakkun.backintime.test.base

import com.github.kitakkun.backintime.runtime.connector.BackInTimeConnector
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow

class MockConnector : BackInTimeConnector {
    private val mutableEventsFromService = mutableListOf<BackInTimeDebugServiceEvent>()
    val eventsFromService get() = mutableEventsFromService.toList()
    private val mutableEventFromDebuggerFlow = MutableSharedFlow<BackInTimeDebuggerEvent>()

    override val connected: Boolean = true
    override val connectedFlow: Flow<Boolean> = flow { emit(true) }

    override suspend fun connect() {
        // no-op
    }

    override suspend fun disconnect() {
        // no-op
    }

    override fun sendEvent(event: BackInTimeDebugServiceEvent) {
        mutableEventsFromService.add(event)
    }

    suspend fun sendEventFromDebugger(event: BackInTimeDebuggerEvent) {
        mutableEventFromDebuggerFlow.emit(event)
    }

    override fun receiveEventAsFlow() = mutableEventFromDebuggerFlow
}
