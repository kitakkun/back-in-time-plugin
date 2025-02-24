package com.kitakkun.backintime.tooling.standalone

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.server.BackInTimeWebSocketServer
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.database.BackInTimeEventConverter
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class StandaloneDebuggerService : BackInTimeDebuggerService {
    override var state: BackInTimeDebuggerService.State by mutableStateOf(
        BackInTimeDebuggerService.State(
            serverIsRunning = false,
            port = null,
            connections = emptyList()
        )
    )
        private set

    private val server = BackInTimeWebSocketServer()
    private val database = BackInTimeDatabaseImpl.instance
    private val backInTimeEventConverter = BackInTimeEventConverter()
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        setupServerMonitoring()
    }

    private fun setupServerMonitoring() {
        coroutineScope.launch {
            while (true) {
                delay(5000)
                state = state.copy(
                    serverIsRunning = server.isRunning,
                    port = server.runningPort,
                )
            }
        }
        coroutineScope.launch {
            server.newSessionFlow.collect { sessionInfo ->
                state = state.copy(
                    connections = state.connections.toMutableList().apply {
                        add(
                            BackInTimeDebuggerService.State.Connection(
                                id = sessionInfo.id,
                                isActive = true,
                                address = sessionInfo.address,
                                port = sessionInfo.port,
                            )
                        )
                    }.distinctBy { it.id }
                )
            }
        }
        coroutineScope.launch {
            server.sessionClosedFlow.collect { sessionInfo ->
                state = state.copy(
                    connections = state.connections.toMutableList().apply {
                        replaceAll {
                            if (it.id == sessionInfo.id) {
                                it.copy(isActive = false)
                            } else {
                                it
                            }
                        }
                    }
                )
            }
        }
        coroutineScope.launch {
            server.eventFromClientFlow.collect {
                backInTimeEventConverter.convertToEntity(it.sessionId, it.event)?.let { eventEntity ->
                    database.insert(eventEntity)
                }
            }
        }
    }

    override fun restartServer(port: Int) {
        server.stop()
        server.start("localhost", port)
    }

    override fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent) {
        coroutineScope.launch {
            server.send(sessionId, event)
        }
    }
} 