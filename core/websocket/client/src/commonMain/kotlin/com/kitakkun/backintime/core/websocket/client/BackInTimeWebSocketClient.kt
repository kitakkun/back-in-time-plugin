package com.kitakkun.backintime.core.websocket.client

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.receiveDeserialized
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.close
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

sealed interface BackInTimeWebSocketClientEvent {
    data class ReceiveDebuggerEvent(val debuggerEvent: BackInTimeDebuggerEvent) : BackInTimeWebSocketClientEvent
    data object CloseSuccessfully : BackInTimeWebSocketClientEvent
    data class CloseWithError(val error: Throwable) : BackInTimeWebSocketClientEvent
}

/**
 * This class is responsible for connecting to the back-in-time debugger server
 */
class BackInTimeWebSocketClient(
    private val host: String,
    private val port: Int,
    engine: HttpClientEngine = CIO.create(),
    client: HttpClient? = null,
) {
    private var session: DefaultClientWebSocketSession? = null

    private val mutableClientEventFlow = MutableSharedFlow<BackInTimeWebSocketClientEvent>()
    val clientEventFlow = mutableClientEventFlow.asSharedFlow()

    private val eventDispatchQueueMutex = Mutex()
    private val eventDispatchQueue = mutableListOf<BackInTimeDebugServiceEvent>()

    private val client = (client ?: HttpClient(engine)).config {
        install(WebSockets) {
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private suspend fun DefaultClientWebSocketSession.handleSession() {
        val receiveJob = launch {
            while (true) {
                val debuggerEvent = receiveDeserialized<BackInTimeDebuggerEvent>()
                mutableClientEventFlow.emit(BackInTimeWebSocketClientEvent.ReceiveDebuggerEvent(debuggerEvent))
            }
        }

        val sendJob = launch {
            while (true) {
                delay(500)
                eventDispatchQueueMutex.withLock {
                    eventDispatchQueue.forEach { sendSerialized(it) }
                    eventDispatchQueue.clear()
                }
            }
        }

        closeReason.invokeOnCompletion { error ->
            session = null

            receiveJob.cancel()
            sendJob.cancel()

            val event = error?.let {
                BackInTimeWebSocketClientEvent.CloseWithError(it)
            } ?: BackInTimeWebSocketClientEvent.CloseSuccessfully

            launch {
                mutableClientEventFlow.emit(event)
            }
        }

        closeReason.await()
    }

    suspend fun awaitClose() {
        session?.closeReason?.await()
    }

    suspend fun openSession() {
        session = client.webSocketSession(
            host = host,
            port = port,
            path = "/backintime",
        )

        session?.launch {
            session?.handleSession()
        }
    }

    fun queueEvent(event: BackInTimeDebugServiceEvent) {
        eventDispatchQueue.add(event)
    }

    suspend fun close() {
        session?.close()
        session = null
    }
}
