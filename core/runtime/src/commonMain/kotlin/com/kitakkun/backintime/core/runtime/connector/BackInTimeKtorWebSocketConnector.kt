package com.kitakkun.backintime.core.runtime.connector

import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClient
import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClientEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flow

/**
 * This class is responsible for sending and receiving events
 */
class BackInTimeKtorWebSocketConnector(
    host: String,
    port: Int,
) : BackInTimeWebSocketConnector {
    private val client: BackInTimeWebSocketClient = BackInTimeWebSocketClient(
        host = host,
        port = port,
    )

    override suspend fun connect(): Flow<BackInTimeDebuggerEvent> {
        client.openSession()

        return flow {
            client.clientEventFlow.filterIsInstance<BackInTimeWebSocketClientEvent.ReceiveDebuggerEvent>().collect {
                emit(it.debuggerEvent)
            }
        }
    }

    override suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent) {
        client.queueEvent(event)
    }

    override suspend fun awaitCloseSession() {
        client.awaitClose()
    }

    override suspend fun close() {
        client.close()
    }
}
