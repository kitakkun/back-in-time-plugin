package com.github.kitakkun.backintime.runtime.connector

import com.github.kitakkun.backintime.websocket.client.BackInTimeWebSocketClient
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class BackInTimeWebSocketClientConnector(host: String, port: Int, private val automaticReconnect: Boolean = true) : BackInTimeConnector {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val client: BackInTimeWebSocketClient = BackInTimeWebSocketClient(host = host, port = port)
    override val connected: Boolean get() = client.isConnected

    override suspend fun connect() {
        while (true) {
            val result = client.connect()
            if (result.isSuccess) break
            delay(3000L)
        }
        // reconnect after disconnection
        if (automaticReconnect) {
            client.closeReason().await()
            connect()
        }
    }

    override suspend fun disconnect() {
        client.close()
    }

    override fun sendEvent(event: BackInTimeDebugServiceEvent) {
        coroutineScope.launch {
            client.send(event)
        }
    }

    override fun receiveEventAsFlow(): Flow<BackInTimeDebuggerEvent> = client.receiveEventAsFlow()
}
