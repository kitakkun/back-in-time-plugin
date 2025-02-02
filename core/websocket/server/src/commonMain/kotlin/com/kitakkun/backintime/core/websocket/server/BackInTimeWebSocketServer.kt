package com.kitakkun.backintime.core.websocket.server

import com.benasher44.uuid.uuid4
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeSessionNegotiationEvent
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.origin
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

class BackInTimeWebSocketServer {
    private var server: ApplicationEngine? = null
    val isRunning: Boolean get() = server?.application?.isActive == true

    private val mutableSessionInfoList = mutableSetOf<SessionInfo>()
    val sessionInfoList: List<SessionInfo> get() = mutableSessionInfoList.toList()

    private val mutableNewSessionFlow = MutableSharedFlow<SessionInfo>()
    val newSessionFlow = mutableNewSessionFlow.asSharedFlow()

    private val mutableSessionClosedFlow = MutableSharedFlow<SessionInfo>()
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
                val sessionId = requestedSessionId ?: uuid4().toString()
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

                val sessionInfo = SessionInfo(
                    id = sessionId,
                    host = this.call.request.origin.remoteHost,
                    port = this.call.request.origin.remotePort,
                    address = this.call.request.origin.remoteAddress,
                )

                closeReason.invokeOnCompletion {
                    mutableSessionInfoList.remove(sessionInfo)

                    sendEventJob.cancel()
                    receiveEventJob.cancel()

                    launch {
                        mutableSessionClosedFlow.emit(sessionInfo)
                    }
                }

                mutableSessionInfoList += sessionInfo
                mutableNewSessionFlow.emit(sessionInfo)

                closeReason.await()
            }
        }
    }
}
