package com.kitakkun.backintime.tooling.flipper

import com.benasher44.uuid.uuid4
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeWebSocketEvent
import kotlinx.datetime.Clock

@JsExport
data class BackInTimeEventData(
    val uuid: String = uuid4().toString(),
    val time: Int = Clock.System.now().epochSeconds.toInt(),
    val payload: BackInTimeWebSocketEvent,
) {
    val label: String
        get() = when (payload) {
            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> "CheckInstanceAliveResult"
            is BackInTimeDebugServiceEvent.Error -> "Error(from App)"
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> "NotifyMethodCall"
            is BackInTimeDebugServiceEvent.NotifyValueChange -> "NotifyValueChange"
            is BackInTimeDebugServiceEvent.Ping -> "Ping(from App)"
            is BackInTimeDebugServiceEvent.RegisterInstance -> "RegisterInstance"
            is BackInTimeDebugServiceEvent.RegisterRelationship -> "RegisterRelationship"
            is BackInTimeDebuggerEvent.CheckInstanceAlive -> "CheckInstanceAlive"
            is BackInTimeDebuggerEvent.Error -> "Error(from Debugger)"
            is BackInTimeDebuggerEvent.ForceSetPropertyValue -> "ForceSetPropertyValue"
            is BackInTimeDebuggerEvent.Ping -> "Ping(from Debugger)"
        }
}
