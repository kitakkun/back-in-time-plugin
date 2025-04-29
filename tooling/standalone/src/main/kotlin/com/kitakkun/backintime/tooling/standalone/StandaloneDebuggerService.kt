package com.kitakkun.backintime.tooling.standalone

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.server.BackInTimeWebSocketServer
import com.kitakkun.backintime.core.websocket.server.BackInTimeWebSocketServerState
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.database.BackInTimeEventConverter
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StandaloneDebuggerService : BackInTimeDebuggerService {
    private val server = BackInTimeWebSocketServer()
    private val database = BackInTimeDatabaseImpl.instance
    private val backInTimeEventConverter = BackInTimeEventConverter()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override val stateFlow: StateFlow<BackInTimeDebuggerService.State> = server.stateFlow.map {
        when (it) {
            is BackInTimeWebSocketServerState.Starting -> BackInTimeDebuggerService.State.Starting
            is BackInTimeWebSocketServerState.Error -> BackInTimeDebuggerService.State.Error(it.message)
            is BackInTimeWebSocketServerState.Stopping -> BackInTimeDebuggerService.State.Stopping
            is BackInTimeWebSocketServerState.Stopped -> BackInTimeDebuggerService.State.Stopped
            is BackInTimeWebSocketServerState.Started -> BackInTimeDebuggerService.State.Started(
                port = it.port,
                connections = it.sessions.map { sessionInfo ->
                    BackInTimeDebuggerService.State.Connection(
                        id = sessionInfo.id,
                        isActive = sessionInfo.isActive,
                        port = sessionInfo.port,
                        address = sessionInfo.address,
                    )
                }
            )
        }
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BackInTimeDebuggerService.State.Stopped,
    )

    private var serverJob: Job? = null

    override fun restartServer(port: Int) {
        serverJob?.cancel()
        serverJob = coroutineScope.launch {
            server.stop()
            server.start("localhost", port)
            coroutineScope.launch {
                server.eventFromClientFlow.collect {
                    backInTimeEventConverter.convertToEntity(it.sessionId, it.event)?.let { eventEntity ->
                        database.insert(eventEntity)
                    }
                }
            }
        }
    }

    override fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent) {
        coroutineScope.launch {
            server.send(sessionId, event)
        }
    }
} 