package io.github.kitakkun.backintime.websocket.server

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.github.kitakkun.backintime.core.websocket.server.Connection
import io.github.kitakkun.backintime.core.websocket.server.ConnectionSpec
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.host
import io.ktor.server.request.port
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

interface BackInTimeWebSocketServer {
    fun start(host: String, port: Int)
    fun stop()
    suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent)
    val newConnectionFlow: Flow<ConnectionSpec>
    val receivedEventFlow: Flow<Pair<String, BackInTimeDebugServiceEvent>>
    val disconnectedConnectionIdFlow: Flow<String>
}

class BackInTimeWebSocketServerImpl : BackInTimeWebSocketServer {
    private var server: EmbeddedServer<*, *>? = null

    suspend fun getActualPort() = server?.engine?.resolvedConnectors()?.firstOrNull()?.port
    suspend fun getActualHost() = server?.engine?.resolvedConnectors()?.firstOrNull()?.host

    val isRunning: Boolean get() = server?.application?.isActive == true
    private val connections = mutableListOf<Connection>()

    private val mutableNewConnectionFlow = MutableSharedFlow<ConnectionSpec>()
    override val newConnectionFlow = mutableNewConnectionFlow.asSharedFlow()

    private val mutableDisconnectedConnectionIdFlow = MutableSharedFlow<String>()
    override val disconnectedConnectionIdFlow = mutableDisconnectedConnectionIdFlow.asSharedFlow()

    private val mutableReceivedEventFlow = MutableSharedFlow<Pair<String, BackInTimeDebugServiceEvent>>()
    override val receivedEventFlow = mutableReceivedEventFlow.asSharedFlow()

    override fun start(host: String, port: Int) {
        server = configureServer(host, port)
        server?.start()
    }

    override fun stop() {
        server?.stop()
        server = null
    }

    override suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent) {
        val session = connections.find { it.id == sessionId }?.session ?: return
        session.send(Json.encodeToString(event))
    }

    private fun configureServer(host: String, port: Int) = embeddedServer(
        factory = CIO,
        port = port,
        host = host,
    ) {
        installWebSocket()
        configureWebSocketRouting(
            onConnect = {
                connections.add(it)
                mutableNewConnectionFlow.emit(
                    ConnectionSpec(
                        it.id,
                        it.session.call.request.host(),
                        it.session.call.request.port()
                    )
                )
                it.session.closeReason.invokeOnCompletion { error ->
                    connections.remove(it)
                    launch {
                        mutableDisconnectedConnectionIdFlow.emit(it.id)
                    }
                }

            },
            onReceiveEvent = { connection, event ->
                mutableReceivedEventFlow.emit(connection.id to event)
            },
        )
    }
}

fun Application.installWebSocket() {
    install(WebSockets) {
        timeoutMillis = 1000 * 60 * 10
    }
}

fun Application.configureWebSocketRouting(
    onConnect: suspend (Connection) -> Unit,
    onReceiveEvent: suspend (Connection, BackInTimeDebugServiceEvent) -> Unit,
) {
    routing {
        webSocket("/backintime") {
            val connection = Connection(this)
            onConnect(connection)

            for (frame in incoming) {
                if (frame !is Frame.Text) continue
                val event = Json.decodeFromString<BackInTimeDebugServiceEvent>(frame.readText())
                onReceiveEvent(connection, event)
            }
        }
    }
}
