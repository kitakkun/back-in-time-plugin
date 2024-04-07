package com.github.kitakkun.backintime.websocket.client

import com.github.kitakkun.backintime.websocket.event.AppToDebuggerEvent
import com.github.kitakkun.backintime.websocket.event.DebuggerToAppEvent
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.coroutines.CoroutineContext

class BackInTimeWebSocketClient : CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    private val client = HttpClient {
        install(WebSockets) {
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    private var session: DefaultClientWebSocketSession? = null
    val connected: Boolean get() = session != null

    private val mutableReceivedEventFlow = MutableSharedFlow<DebuggerToAppEvent>()
    val receivedEventFlow = mutableReceivedEventFlow.asSharedFlow()

    fun connect(host: String, port: Int) {
        launch {
            client.webSocket(
                host = host,
                port = port,
                path = "/backintime",
            ) {
                session = this

                incoming
                    .consumeAsFlow()
                    .filterIsInstance<Frame.Text>()
                    .map { Json.decodeFromString<DebuggerToAppEvent>(it.readText()) }
                    .collect {
                        mutableReceivedEventFlow.emit(it)
                    }
            }
        }
    }

    fun send(event: AppToDebuggerEvent) {
        launch {
            session?.outgoing?.send(Frame.Text(Json.encodeToString(event)))
        }
    }

    fun close() {
        launch {
            session?.close()
        }
        session = null
    }
}
