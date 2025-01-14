package com.kitakkun.backintime.tooling.idea.service

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.kitakkun.backintime.core.websocket.server.BackInTimeWebSocketServer
import com.kitakkun.backintime.tooling.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.shared.BackInTimeDatabase
import com.kitakkun.backintime.tooling.shared.BackInTimeDebuggerService
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

    override var state: BackInTimeDebuggerService.State by mutableStateOf(BackInTimeDebuggerService.State(serverIsRunning = false, emptyList()))
        private set

    private val server: BackInTimeWebSocketServer = BackInTimeWebSocketServer()
    private val database: BackInTimeDatabase = BackInTimeDatabaseImpl.instance

    private val backInTimeEventConverter = BackInTimeEventConverter()

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        coroutineScope.launch {
            while (true) {
                delay(5000)
                state = state.copy(serverIsRunning = server.isRunning)
            }
        }
        coroutineScope.launch {
            server.newSessionFlow.collect {
                state = state.copy(connections = server.sessionInfoList.map { it.id })
                thisLogger().warn(it.toString())
            }
        }
        coroutineScope.launch {
            server.eventFromClientFlow.collect {
                val event = backInTimeEventConverter.convert(it.event) ?: return@collect
                database.saveEvent(sessionId = it.sessionId, event = event)
            }
        }
    }

    override fun restartServer(port: Int) {
        server.stop()
        server.start("localhost", port)
        thisLogger().warn("Started back-in-time debugger server at localhost:$port")
    }
}
