package com.kitakkun.backintime.tooling.idea.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.server.BackInTimeWebSocketServer
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDatabase
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Service(Service.Level.APP)
class BackInTimeDebuggerServiceImpl : BackInTimeDebuggerService {
    companion object {
        fun getInstance(): BackInTimeDebuggerService = ApplicationManager.getApplication().service<BackInTimeDebuggerServiceImpl>()
    }

    override var state: BackInTimeDebuggerService.State by mutableStateOf(
        BackInTimeDebuggerService.State(
            serverIsRunning = false,
            port = null,
            connections = emptyList()
        )
    )
        private set

    private val server: BackInTimeWebSocketServer = BackInTimeWebSocketServer()
    private val database: BackInTimeDatabase = BackInTimeDatabaseImpl.instance
    private val backInTimeEventConverter = BackInTimeEventConverter()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
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
                thisLogger().warn(sessionInfo.toString())
            }
        }
        coroutineScope.launch {
            server.sessionClosedFlow.collect { sessionInfo ->
                thisLogger().warn("disconnected: $sessionInfo")
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
        thisLogger().warn("Started back-in-time debugger server at localhost:$port")
    }

    override fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent) {
        thisLogger().warn("Sending event... sessionId: $sessionId, event: $event")
        coroutineScope.launch {
            server.send(sessionId, event)
        }
    }
}
