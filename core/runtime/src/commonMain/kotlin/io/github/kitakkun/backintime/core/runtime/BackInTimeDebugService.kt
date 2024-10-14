package io.github.kitakkun.backintime.core.runtime

import io.github.kitakkun.backintime.core.runtime.connector.BackInTimeWebSocketConnector
import io.github.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent
import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent

interface BackInTimeDebugService {
    fun startService()
    fun stopService()
    fun setConnector(connector: BackInTimeWebSocketConnector)
    fun processInstanceEvent(event: BackInTimeDebuggableInstanceEvent)
    fun processDebuggerEvent(event: BackInTimeDebuggerEvent)
}
