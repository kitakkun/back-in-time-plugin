package com.kitakkun.backintime.core.websocket.server

import com.benasher44.uuid.uuid4
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeSessionNegotiationEvent
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.ApplicationStarted
import io.ktor.server.application.ApplicationStopped
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

private sealed interface ServerApplicationStatus {
    data object Starting : ServerApplicationStatus
    data class Started(val host: String, val port: Int) : ServerApplicationStatus
    data class Error(val message: String) : ServerApplicationStatus
    data object Stopping : ServerApplicationStatus
    data object Stopped : ServerApplicationStatus
}

sealed interface BackInTimeWebSocketServerState {
    data object Starting : BackInTimeWebSocketServerState
    data class Started(val host: String, val port: Int, val sessions: List<SessionInfo>) : BackInTimeWebSocketServerState
    data class Error(val message: String) : BackInTimeWebSocketServerState
    data object Stopping : BackInTimeWebSocketServerState
    data object Stopped : BackInTimeWebSocketServerState
}

class BackInTimeWebSocketServer {
    private var serverInstance: EmbeddedServer<*, *>? = null

    private val sessions = MutableStateFlow<List<SessionInfo>>(emptyList())
    private val serverStatus = MutableStateFlow<ServerApplicationStatus>(ServerApplicationStatus.Stopped)

    val stateFlow = combine(
        serverStatus,
        sessions,
    ) { status, sessions ->
        when (status) {
            is ServerApplicationStatus.Stopped -> BackInTimeWebSocketServerState.Stopped
            is ServerApplicationStatus.Started -> BackInTimeWebSocketServerState.Started(status.host, status.port, sessions)
            is ServerApplicationStatus.Error -> BackInTimeWebSocketServerState.Error(status.message)
            is ServerApplicationStatus.Starting -> BackInTimeWebSocketServerState.Starting
            is ServerApplicationStatus.Stopping -> BackInTimeWebSocketServerState.Stopping
        }
    }.stateIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackInTimeWebSocketServerState.Stopped,
    )

    private val mutableEventFromClientFlow = MutableSharedFlow<EventFromClient>()
    val eventFromClientFlow = mutableEventFromClientFlow.asSharedFlow()

    private val sendEventFlow = MutableSharedFlow<EventToClient>()

    fun start(host: String, port: Int) {
        if (serverStatus.value !is ServerApplicationStatus.Stopped && serverStatus.value !is ServerApplicationStatus.Error) return

        serverStatus.update { ServerApplicationStatus.Starting }

        serverInstance = configureServer(host, port)
        try {
            serverInstance?.start()
        } catch (e: Throwable) {
            serverStatus.update { ServerApplicationStatus.Error(e.cause?.message ?: "Unknown error") }
        }
    }

    fun stop() {
        if (serverStatus.value !is ServerApplicationStatus.Started) return
        serverStatus.update { ServerApplicationStatus.Stopping }
        serverInstance?.stop()
        serverInstance = null
        sessions.update { emptyList() }
    }

    suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent) {
        sendEventFlow.emit(EventToClient(sessionId, event))
    }

    private fun configureServer(host: String, port: Int) = embeddedServer(
        factory = CIO.apply {
        },
        port = port,
        host = host,
    ) {
        install(WebSockets) {
            timeoutMillis = 1000 * 60 * 10
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }

        monitor.subscribe(ApplicationStarted) {
            launch {
                val connector = engine.resolvedConnectors().firstOrNull() ?: return@launch
                serverStatus.update {
                    ServerApplicationStatus.Started(
                        host = connector.host,
                        port = connector.port
                    )
                }
            }
        }

        monitor.subscribe(ApplicationStopped) {
            serverStatus.update { ServerApplicationStatus.Stopped }
            monitor.unsubscribe(ApplicationStarted) {}
            monitor.unsubscribe(ApplicationStopped) {}
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
