package io.github.kitakkun.backintime.debugger.core.server

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import io.github.kitakkun.backintime.core.websocket.server.ConnectionSpec
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepository
import io.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

interface BackInTimeDebuggerService {
    fun start(host: String, port: Int)
    fun stop()

    fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent)
    val newConnectionFlow: Flow<ConnectionSpec>
    val serviceStateFlow: StateFlow<BackInTimeDebuggerServiceState>
}

class BackInTimeDebuggerServiceImpl(
    private val server: BackInTimeWebSocketServer,
    private val incomingEventProcessor: IncomingEventProcessor,
    private val sessionInfoRepository: SessionInfoRepository,
) : BackInTimeDebuggerService, CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO + SupervisorJob()

    override val newConnectionFlow = server.newConnectionFlow

    private val mutableServiceStateFlow: MutableStateFlow<BackInTimeDebuggerServiceState> = MutableStateFlow(BackInTimeDebuggerServiceState.Uninitialized)
    override val serviceStateFlow = mutableServiceStateFlow.asStateFlow()

    override fun start(host: String, port: Int) {
        try {
            server.start(host, port)

            launch {
                server.newConnectionFlow.map { it.id }.collect { sessionId ->
                    val existingEntry = sessionInfoRepository.select(sessionId)
                    if (existingEntry == null) {
                        sessionInfoRepository.insert(sessionId = sessionId)
                    } else {
                        sessionInfoRepository.markAsConnected(sessionId)
                    }
                }
            }
            launch {
                server.disconnectedConnectionIdFlow.collect { disconnectedConnectionId ->
                    sessionInfoRepository.markAsDisconnected(disconnectedConnectionId)
                }
            }
            launch {
                server.receivedEventFlow.collect { (sessionId, event) ->
                    incomingEventProcessor.processEvent(sessionId, event)
                }
            }

            mutableServiceStateFlow.value = BackInTimeDebuggerServiceState.Running(host, port)
        } catch (e: Throwable) {
            mutableServiceStateFlow.value = BackInTimeDebuggerServiceState.Error(e)
        }
    }

    override fun stop() {
        coroutineContext.cancelChildren()
        server.stop()
    }

    override fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent) {
        launch {
            server.send(sessionId, event)
        }
    }
}
