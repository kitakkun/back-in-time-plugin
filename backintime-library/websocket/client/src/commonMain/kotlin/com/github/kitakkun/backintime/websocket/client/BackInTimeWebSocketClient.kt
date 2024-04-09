package com.github.kitakkun.backintime.websocket.client

import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * This class is responsible for connecting to the back-in-time debugger server
 */
class BackInTimeWebSocketClient {
    private val client = HttpClient {
        install(WebSockets) {
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    val connected: Boolean get() = session != null

    private val mutableReceivedEventFlow = MutableSharedFlow<BackInTimeDebuggerEvent>()
    val receivedEventFlow = mutableReceivedEventFlow.asSharedFlow()

    suspend fun connectUntilSuccess(host: String, port: Int) {
        suspendCoroutine { continuation ->
            CoroutineScope(Dispatchers.IO).launch {
                var retryCount = 0
                while (isActive) {
                    try {
                        client.webSocket(
                            host = host,
                            port = port,
                            path = "/backintime",
                        ) {
                            session = this

                            retryCount = 0
                            continuation.resume(Unit)

                            incoming
                                .consumeAsFlow()
                                .filterIsInstance<Frame.Text>()
                                .map { Json.decodeFromString<BackInTimeDebuggerEvent>(it.readText()) }
                                .collect {
                                    mutableReceivedEventFlow.emit(it)
                                }
                        }
                    } catch (e: Throwable) {
                        session = null
                        retryCount++
                        delay((retryCount * 1000L).coerceAtMost(5000L))
                    }
                }
            }
        }
    }

    suspend fun send(event: BackInTimeDebugServiceEvent) {
        session?.outgoing?.send(Frame.Text(Json.encodeToString(event)))
    }

    suspend fun close() {
        session?.close()
        session = null
    }
}
