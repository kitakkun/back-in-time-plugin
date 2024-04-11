package com.github.kitakkun.backintime.runtime.connector

import com.github.kitakkun.backintime.websocket.client.BackInTimeWebSocketClient
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class BackInTimeWebSocketClientConnector(
    private val host: String,
    private val port: Int,
) : BackInTimeConnector {
    private val client: BackInTimeWebSocketClient = BackInTimeWebSocketClient()
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    override val connectedFlow: Flow<Boolean> = client.connectedFlow
    override val isConnected: Boolean get() = client.connectedFlow.value

    override fun connect() {
        coroutineScope.launch {
            client.connectUntilSuccess(host, port)
        }
    }

    override fun disconnect() {
        coroutineScope.launch {
            client.close()
        }
    }

    override fun sendEvent(event: BackInTimeDebugServiceEvent) {
        coroutineScope.launch {
            client.send(event)
        }
    }

    override fun receiveEventAsFlow(): Flow<BackInTimeDebuggerEvent> {
        return flow {
            client.receivedEventFlow.collect {
                emit(it)
            }
        }
    }
}
