package com.kitakkun.backintime.core.runtime

import com.kitakkun.backintime.core.runtime.connector.BackInTimeWebSocketConnector
import com.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent

interface BackInTimeDebugService {
    fun startService()
    fun stopService()
    fun setConnector(connector: BackInTimeWebSocketConnector)
    fun processInstanceEvent(event: BackInTimeDebuggableInstanceEvent)
    fun processDebuggerEvent(event: BackInTimeDebuggerEvent)
}
