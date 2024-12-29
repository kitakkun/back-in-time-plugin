package com.kitakkun.backintime.core.websocket.server

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeSessionNegotiationEvent
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.receiveDeserialized
import io.ktor.server.websocket.sendSerialized
import io.ktor.server.websocket.webSocket
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BackInTimeWebSocketServer {
    private var server: ApplicationEngine? = null
    val isRunning: Boolean get() = server?.application?.isActive == true

    private val mutableSessionIds = mutableSetOf<String>()
    val sessionIds: List<String> get() = mutableSessionIds.toList()

    private val mutableNewSessionIdFlow = MutableSharedFlow<String>()
    val newSessionIdFlow = mutableNewSessionIdFlow.asSharedFlow()

    private val mutableSessionClosedFlow = MutableSharedFlow<String>()
    val sessionClosedFlow = mutableSessionClosedFlow.asSharedFlow()

    private val mutableEventFromClientFlow = MutableSharedFlow<EventFromClient>()
    val eventFromClientFlow = mutableEventFromClientFlow.asSharedFlow()

    private val sendEventFlow = MutableSharedFlow<EventToClient>()

    fun start(host: String, port: Int) {
        server = configureServer(host, port)
        server?.start()
    }

    fun stop() {
        server?.stop()
        server = null
    }

    suspend fun send(sessionId: String, event: BackInTimeDebuggerEvent) {
        sendEventFlow.emit(EventToClient(sessionId, event))
    }

    @OptIn(ExperimentalUuidApi::class)
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
                // sessionId negotiation
                val requestedSessionId = receiveDeserialized<BackInTimeSessionNegotiationEvent.Request>().sessionId
                val sessionId = requestedSessionId ?: Uuid.random().toString()
                sendSerialized(BackInTimeSessionNegotiationEvent.Accept(sessionId))

                val sendEventJob = launch {
                    sendEventFlow.filter { it.sessionId == sessionId }.collect {
                        sendSerialized(it.event)
                    }
                }

                val receiveEventJob = launch {
                    while (true) {
                        val event = receiveDeserialized<BackInTimeDebugServiceEvent>()
                        mutableEventFromClientFlow.emit(EventFromClient(sessionId, event))
                    }
                }

                closeReason.invokeOnCompletion {
                    mutableSessionIds.remove(sessionId)

                    sendEventJob.cancel()
                    receiveEventJob.cancel()

                    launch {
                        mutableSessionClosedFlow.emit(sessionId)
                    }
                }

                mutableSessionIds += sessionId
                mutableNewSessionIdFlow.emit(sessionId)

                closeReason.await()
            }
        }
    }
}
