package com.github.kitakkun.backintime.debugger.data.server

import com.github.kitakkun.backintime.debugger.data.coroutines.IOScope
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class BackInTimeDebuggerService(
    private val server: BackInTimeWebSocketServer,
    private val incomingEventProcessor: IncomingEventProcessor,
) : CoroutineScope by IOScope() {
    private val mutableSessionIds = mutableListOf<String>()
    val sessionIds: List<String> get() = mutableSessionIds

    init {
        launch {
            server.connectionEstablishedFlow.toList(mutableSessionIds)
        }
        launch {
            server.receivedEventFlow.collect { (sessionId, event) ->
                val result = incomingEventProcessor.processEvent(sessionId, event) ?: return@collect
                server.send(sessionId, result)
            }
        }
    }

    fun start(host: String, port: Int) {
        server.start(host, port)
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
