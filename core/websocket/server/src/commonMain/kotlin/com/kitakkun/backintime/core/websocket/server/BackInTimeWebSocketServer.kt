package com.kitakkun.backintime.core.websocket.server

import com.benasher44.uuid.uuid4
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeSessionNegotiationEvent
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStarting
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.origin
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private enum class ServerApplicationStatus {
    STARTING,
    STARTED,
    STOPPING,
    STOPPED,
}

sealed class BackInTimeWebSocketServerState {
    data object Starting : BackInTimeWebSocketServerState()
    data class Started(val host: String, val port: Int, val sessions: List<SessionInfo>) : BackInTimeWebSocketServerState()
    data object Stopping : BackInTimeWebSocketServerState()
    data object Stopped : BackInTimeWebSocketServerState()
}

class BackInTimeWebSocketServer {
    private var serverInstance: EmbeddedServer<*, *>? = null

    private val sessions = MutableStateFlow<List<SessionInfo>>(emptyList())
    private val serverStatus = MutableStateFlow(ServerApplicationStatus.STOPPED)

    val stateFlow = combine(
        serverStatus,
        sessions,
    ) { status, sessions ->
        when (status) {
            ServerApplicationStatus.STARTING -> BackInTimeWebSocketServerState.Starting
            ServerApplicationStatus.STOPPING -> BackInTimeWebSocketServerState.Stopping
            ServerApplicationStatus.STOPPED -> BackInTimeWebSocketServerState.Stopped
            ServerApplicationStatus.STARTED -> {
                val host = serverInstance?.engineConfig?.connectors?.firstOrNull()?.host ?: "localhost"
                val port = serverInstance?.engineConfig?.connectors?.firstOrNull()?.port ?: 0
                BackInTimeWebSocketServerState.Started(host, port, sessions)
            }
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackInTimeWebSocketServerState.Stopped,
    )

    private val mutableEventFromClientFlow = MutableSharedFlow<EventFromClient>()
    val eventFromClientFlow = mutableEventFromClientFlow.asSharedFlow()

    private val sendEventFlow = MutableSharedFlow<EventToClient>()


    suspend fun start(host: String, port: Int) {
        serverInstance = configureServer(host, port)
        serverInstance?.startSuspend()
    }

    suspend fun stop() {
        serverInstance?.stopSuspend()
        serverInstance = null
    }

    suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent) {
        sendEventFlow.emit(EventToClient(sessionId, event))
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

        monitor.subscribe(ApplicationStarting) {
            serverStatus.update { ServerApplicationStatus.STARTING }
        }

        monitor.subscribe(ApplicationStarted) {
            serverStatus.update { ServerApplicationStatus.STARTED }
        }

        monitor.subscribe(ApplicationStopping) {
            serverStatus.update { ServerApplicationStatus.STOPPING }
        }

        monitor.subscribe(ApplicationStopped) {
            serverStatus.update { ServerApplicationStatus.STOPPED }
        }

        routing {
            webSocket("/backintime") {
                // sessionId negotiation
                val requestedSessionId = receiveDeserialized<BackInTimeSessionNegotiationEvent.Request>().sessionId
                val sessionId = requestedSessionId ?: uuid4().toString()
                sendSerialized(BackInTimeSessionNegotiationEvent.Accept(sessionId))

                val sendEventJob = launch {
                    sendEventFlow.filter { it.sessionId == sessionId }.collect {
                        sendSerialized(it.event)
                    }
                }

                val receiveEventJob = launch {
                    try {
                        while (true) {
                            val event = receiveDeserialized<BackInTimeDebugServiceEvent>()
                            mutableEventFromClientFlow.emit(EventFromClient(sessionId, event))
                        }
                    } catch (e: ClosedReceiveChannelException) {
                        // Prevent the parent coroutine scope from terminating
                        // and ensure that the code following closeReason.await() gets executed.
                    }
                }

                val sessionInfo = SessionInfo(
                    id = sessionId,
                    host = this.call.request.origin.remoteHost,
                    port = this.call.request.origin.remotePort,
                    address = this.call.request.origin.remoteAddress,
                    isActive = true,
                )

                sessions.update {
                    (it + sessionInfo).distinctBy { it.id }
                }

                closeReason.await()

                sessions.update {
                    it.map {
                        if (it.id == sessionId) {
                            it.copy(isActive = false)
                        } else {
                            it
                        }
                    }
                }

                sendEventJob.cancel()
                receiveEventJob.cancel()
            }
        }
    }
}
