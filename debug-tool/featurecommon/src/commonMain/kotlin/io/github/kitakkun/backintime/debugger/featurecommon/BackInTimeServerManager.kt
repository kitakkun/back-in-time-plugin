package io.github.kitakkun.backintime.debugger.featurecommon

import io.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServerImpl
import kotlinx.coroutines.flow.MutableStateFlow

class BackInTimeServerManager() {
    private val backInTimeWebSocketServer: BackInTimeWebSocketServerImpl = BackInTimeWebSocketServerImpl()
    val isRunningFlow = MutableStateFlow(backInTimeWebSocketServer.isRunning)

    fun start(host: String, port: Int) {
        backInTimeWebSocketServer.start(host, port)
        isRunningFlow.value = backInTimeWebSocketServer.isRunning
    }

    fun stop() {
        backInTimeWebSocketServer.stop()
        isRunningFlow.value = backInTimeWebSocketServer.isRunning
    }
}
