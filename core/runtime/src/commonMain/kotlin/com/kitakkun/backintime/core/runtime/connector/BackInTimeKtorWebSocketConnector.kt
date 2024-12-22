package com.kitakkun.backintime.core.runtime.connector

import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClient
import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClientListener
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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

        return channelFlow {
            suspendCoroutine {
                val listener = object : BackInTimeWebSocketClientListener {
                    override fun onReceive(event: BackInTimeDebuggerEvent) {
                        launch {
                            send(event)
                        }
                    }

                    override fun onClose() {
                        it.resume(Unit)
                        client.removeListener(this)
                    }

                    override fun onCloseByError(error: Throwable) {
                        it.resume(Unit)
                        client.removeListener(this)
                    }
                }

                client.addListener(listener)
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
