package com.github.kitakkun.backintime.websocket.client

import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * This class is responsible for connecting to the back-in-time debugger server
 */
class BackInTimeWebSocketClient(
    private val host: String,
    private val port: Int,
    engine: HttpClientEngine = CIO.create(),
    client: HttpClient? = null,
) {
    private var sessionId: String? = null
    private val client = client ?: HttpClient(engine) {
        install(WebSockets) {
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    val isConnected: Boolean get() = session != null

    suspend fun connect(): Result<Unit> {
        return try {
            session = client.webSocketSession(host = host, port = port, path = "/backintime") {
                if (sessionId != null) {
                    this.parameter("sessionId", sessionId)
                }
            }
            session?.closeReason?.invokeOnCompletion {
                session = null
            }
            sessionId = (receiveEventAsFlow().first() as BackInTimeDebuggerEvent.SessionOpened).sessionUUID
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure(e)
        }
    }

    fun closeReason() = session?.closeReason ?: error("Not connected")

    fun receiveEventAsFlow() = session?.incoming?.receiveAsFlow()
        ?.filterIsInstance<Frame.Text>()
        ?.map {
            Json.decodeFromString<BackInTimeDebuggerEvent>(it.readText())
        } ?: error("Not connected")

    suspend fun send(event: BackInTimeDebugServiceEvent) {
        session?.send(Json.encodeToString(event))
    }

    suspend fun close() {
        session?.close()
        session = null
    }
}
