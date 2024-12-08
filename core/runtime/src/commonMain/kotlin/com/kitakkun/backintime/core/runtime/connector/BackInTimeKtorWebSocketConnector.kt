package com.kitakkun.backintime.core.runtime.connector

import com.kitakkun.backintime.core.websocket.client.BackInTimeWebSocketClient
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * This class is responsible for sending and receiving events
 */
class BackInTimeKtorWebSocketConnector(
    host: String,
    port: Int,
) : BackInTimeWebSocketConnector {
    private var currentSession: DefaultClientWebSocketSession? = null

    private val client: BackInTimeWebSocketClient = BackInTimeWebSocketClient(
        host = host,
        port = port,
    )

    override suspend fun connect(): Flow<BackInTimeDebuggerEvent> {
        val session = client.openSession()
        currentSession = session

        return session.incoming
            .receiveAsFlow()
            .filterIsInstance<Frame.Text>()
            .map { Json.decodeFromString(it.readText()) }
    }

    override suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent) {
        currentSession?.send(Json.encodeToString<BackInTimeDebugServiceEvent>(event))
    }

    override suspend fun awaitCloseSession() {
        currentSession?.closeReason?.await()
        currentSession = null
    }

    override suspend fun close() {
        currentSession?.close()
        currentSession = null
    }
}
