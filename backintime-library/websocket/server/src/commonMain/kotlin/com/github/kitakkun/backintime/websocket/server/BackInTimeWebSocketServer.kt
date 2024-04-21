package com.github.kitakkun.backintime.websocket.server

import com.benasher44.uuid.uuid4
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class Connection(
    val session: DefaultWebSocketServerSession,
) {
    val spec: ConnectionSpec = ConnectionSpec(
        host = session.call.request.local.remoteHost,
        port = session.call.request.local.remotePort,
        address = session.call.request.local.remoteAddress,
    )
}

data class ConnectionSpec(
    val host: String,
    val port: Int,
    val address: String,
    val id: String = uuid4().toString(),
)

data class ServerSpec(
    val host: String,
    val port: Int,
)

class BackInTimeWebSocketServer {
    private var server: ApplicationEngine? = null

    private val mutableConnectionsFlow = MutableStateFlow<List<Connection>>(emptyList())
    private val connectionsFlow = mutableConnectionsFlow.asStateFlow()
    val connectionSpecsFlow = connectionsFlow.map { it.map { it.spec } }

    private val mutableServerSpecFlow = MutableStateFlow<ServerSpec?>(null)
    val serverSpecFlow = mutableServerSpecFlow.asStateFlow()

    private val mutableReceivedEventFlow = MutableSharedFlow<Pair<String, BackInTimeDebugServiceEvent>>()
    val receivedEventFlow = mutableReceivedEventFlow.asSharedFlow()

    fun start(host: String, port: Int) {
        server = configureServer(host, port)
        server?.start()
        mutableServerSpecFlow.value = ServerSpec(host, port)
    }

    suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent) {
        val session = connectionsFlow.value.find { it.spec.id == sessionId }?.session ?: return
        session.send(Json.encodeToString(event))
    }

    fun stop() {
        server?.stop()
        server = null
        mutableServerSpecFlow.value = null
    }

    private fun configureServer(host: String, port: Int) = embeddedServer(
        factory = CIO,
        port = port,
        host = host,
    ) {
        installWebSocket()
        configureWebSocketRouting(
            onConnect = {
                mutableConnectionsFlow.value = connectionsFlow.value + it
            },
            onReceiveEvent = { connection, event ->
                mutableReceivedEventFlow.emit(connection.spec.id to event)
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
