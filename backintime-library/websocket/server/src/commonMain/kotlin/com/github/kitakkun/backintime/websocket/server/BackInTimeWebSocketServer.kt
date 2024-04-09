package com.github.kitakkun.backintime.websocket.server

import com.benasher44.uuid.uuid4
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
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
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class Connection(
    val session: DefaultWebSocketServerSession,
    val id: String = uuid4().toString(),
)

class BackInTimeWebSocketServer {
    private var server: ApplicationEngine? = null

    suspend fun getActualPort() = server?.resolvedConnectors()?.firstOrNull()?.port
    suspend fun getActualHost() = server?.resolvedConnectors()?.firstOrNull()?.host

    val isRunning: Boolean get() = server?.application?.isActive == true
    private val connections = mutableListOf<Connection>()

    private val mutableConnectionEstablishedFlow = MutableSharedFlow<String>()
    val connectionEstablishedFlow = mutableConnectionEstablishedFlow.asSharedFlow()

    private val mutableReceivedEventFlow = MutableSharedFlow<Pair<String, BackInTimeDebugServiceEvent>>()
    val receivedEventFlow = mutableReceivedEventFlow.asSharedFlow()

    fun start(port: Int) {
        server = embeddedServer(
            factory = CIO,
            port = port,
            host = "127.0.0.1",
        ) {
            install(WebSockets) {
                timeoutMillis = 1000 * 60 * 10
            }
            routing {
                webSocket("/backintime") {
                    val connection = Connection(this)
                    connections += connection

                    mutableConnectionEstablishedFlow.emit(connection.id)

                    incoming
                        .consumeAsFlow()
                        .filterIsInstance<Frame.Text>()
                        .map { Json.decodeFromString<BackInTimeDebugServiceEvent>(it.readText()) }
                        .collect {
                            mutableReceivedEventFlow.emit(connection.id to it)
                        }
                }
            }
        }

        server?.start()
    }

    suspend fun send(event: BackInTimeDebuggerEvent) {
        connections.forEach {
            it.session.send(Json.encodeToString(event))
        }
    }

    fun stop() {
        server?.stop()
        server = null
    }
}
