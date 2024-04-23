package com.github.kitakkun.backintime.runtime.connector

import com.github.kitakkun.backintime.websocket.client.BackInTimeWebSocketClient
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class BackInTimeWebSocketClientConnector(host: String, port: Int) : BackInTimeConnector {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val client: BackInTimeWebSocketClient = BackInTimeWebSocketClient(host = host, port = port)
    override val connected: Boolean get() = client.isConnected
    private val mutableIsConnectedFlow = MutableStateFlow(false)
    override val connectedFlow: Flow<Boolean> get() = mutableIsConnectedFlow.asSharedFlow()

    override fun connect() {
        coroutineScope.launch {
            while (true) {
                val result = client.connect()
                if (result.isSuccess) break
                delay(3000L)
            }
            mutableIsConnectedFlow.value = true
            // reconnect after disconnection
            client.closeReason().await()
            mutableIsConnectedFlow.value = false
            connect()
        }
    }

    override fun disconnect() {
        coroutineScope.launch {
            client.close()
            mutableIsConnectedFlow.value = false
        }
    }

    override fun sendEvent(event: BackInTimeDebugServiceEvent) {
        coroutineScope.launch {
            client.send(event)
        }
    }

    override fun receiveEventAsFlow(): Flow<BackInTimeDebuggerEvent> = client.receiveEventAsFlow()
}
