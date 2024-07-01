package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent

interface BackInTimeDebugService {
    fun startService()
    fun stopService()
    fun processInstanceEvent(event: BackInTimeDebuggableInstanceEvent)
    fun processDebuggerEvent(event: BackInTimeDebuggerEvent)
}