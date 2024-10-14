package io.github.kitakkun.backintime.runtime

import io.github.kitakkun.backintime.runtime.connector.BackInTimeWebSocketConnector
import io.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent
import io.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent

interface BackInTimeDebugService {
    fun startService()
    fun stopService()
    fun setConnector(connector: BackInTimeWebSocketConnector)
    fun processInstanceEvent(event: BackInTimeDebuggableInstanceEvent)
    fun processDebuggerEvent(event: BackInTimeDebuggerEvent)
}
