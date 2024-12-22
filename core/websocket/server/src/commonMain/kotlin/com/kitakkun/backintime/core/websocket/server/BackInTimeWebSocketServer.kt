package com.kitakkun.backintime.core.websocket.server

import com.benasher44.uuid.uuid4
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.send
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

data class Connection(
    val session: DefaultWebSocketServerSession,
    val id: String = uuid4().toString(),
)

class BackInTimeWebSocketServer {
    private var server: ApplicationEngine? = null

    val isRunning: Boolean get() = server?.application?.isActive == true
    private val connections = mutableListOf<Connection>()

    private val mutableConnectionEstablishedFlow = MutableSharedFlow<String>()
    val connectionEstablishedFlow = mutableConnectionEstablishedFlow.asSharedFlow()

    private val mutableReceivedEventFlow = MutableSharedFlow<Pair<String, BackInTimeDebugServiceEvent>>()
    val receivedEventFlow = mutableReceivedEventFlow.asSharedFlow()

    fun start(host: String, port: Int) {
        server = configureServer(host, port)
        server?.start()
    }

    suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent) {
        val session = connections.find { it.id == sessionId }?.session ?: return
        session.send(Json.encodeToString(event))
    }

    fun stop() {
        server?.stop()
        server = null
    }

    private fun configureServer(host: String, port: Int) = embeddedServer(
        factory = CIO,
        port = port,
        host = host,
    ) {
        install(WebSockets) {
            timeoutMillis = 1000 * 60 * 10
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        routing {
            webSocket("/backintime") {
                val connection = Connection(this)
                connections.add(connection)
                mutableConnectionEstablishedFlow.emit(connection.id)

                while (true) {
                    val event = receiveDeserialized<BackInTimeDebugServiceEvent>()
                    mutableReceivedEventFlow.emit(connection.id to event)
                }
            }
        }
    }
}
