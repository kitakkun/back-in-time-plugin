package com.github.kitakkun.backintime.debugger.data.server

import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BackInTimeDebuggerService(
    private val server: BackInTimeWebSocketServer,
    private val incomingEventProcessor: IncomingEventProcessor,
) : CoroutineScope by IOScope() {
    val connectionSpecsFlow = server.connectionSpecsFlow

    private val mutableServiceStateFlow: MutableStateFlow<BackInTimeDebuggerServiceState> = MutableStateFlow(BackInTimeDebuggerServiceState.Uninitialized)
    val serviceStateFlow = mutableServiceStateFlow.asStateFlow()

    init {
        launch {
            server.receivedEventFlow.collect { (sessionId, event) ->
                val result = incomingEventProcessor.processEvent(sessionId, event) ?: return@collect
                server.send(sessionId, result)
            }
        }
    }

    fun start(host: String, port: Int) {
        val result = server.start(host, port)
        when {
            result.isSuccess -> mutableServiceStateFlow.value = BackInTimeDebuggerServiceState.Running(host, port)
            result.isFailure -> mutableServiceStateFlow.value = BackInTimeDebuggerServiceState.Error(result.exceptionOrNull()!!)
        }
    }

    fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent) {
        launch {
            server.send(sessionId, event)
        }
    }

    fun stop() {
        server.stop()
    }
}
