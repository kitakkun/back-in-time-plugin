package com.github.kitakkun.backintime.runtime.connector

import com.github.kitakkun.backintime.websocket.client.BackInTimeWebSocketClient
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * This class is responsible for sending, queueing, and receiving events
 */
class BackInTimeKtorWebSocketConnector(
    host: String,
    port: Int,
) : BackInTimeWebSocketConnector {
    private var currentSession: DefaultClientWebSocketSession? = null
    private val eventDispatchQueue = mutableListOf<BackInTimeDebugServiceEvent>()

    private val client: BackInTimeWebSocketClient = BackInTimeWebSocketClient(
        host = host,
        port = port,
    )

    override suspend fun connect(): Flow<BackInTimeDebuggerEvent> {
        val session = client.openSession()
        currentSession = session

        if (eventDispatchQueue.isNotEmpty()) {
            session.launch {
                eventDispatchQueue.forEach { session.send(Json.encodeToString(it)) }
            }
        }

        return session.incoming
            .receiveAsFlow()
            .filterIsInstance<Frame.Text>()
            .map { Json.decodeFromString(it.readText()) }
    }

    override suspend fun sendOrQueueEvent(event: BackInTimeDebugServiceEvent) {
        currentSession?.send(Json.encodeToString<BackInTimeDebugServiceEvent>(event))
            ?: eventDispatchQueue.add(event)
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
