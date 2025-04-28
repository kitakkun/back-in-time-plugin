package com.kitakkun.backintime.tooling.core.shared

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface BackInTimeDebuggerService {
    data class State(
        val serverIsRunning: Boolean = false,
        val port: Int? = null,
        val connections: List<Connection> = emptyList(),
    ) {
        data class Connection(
            val id: String,
            val isActive: Boolean,
            val port: Int,
            val address: String,
        )
    }

    val stateFlow: StateFlow<State>

    fun restartServer(port: Int)

    fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent)

    fun backInTime(sessionId: String, instanceId: String, values: Map<String, String>) {
        values.forEach { (signature, jsonValue) ->
            sendEvent(
                sessionId, BackInTimeDebuggerEvent.ForceSetPropertyValue(
                    targetInstanceId = instanceId,
                    propertySignature = signature,
                    jsonValue = jsonValue,
                )
            )
        }
    }

    companion object {
        val Dummy = object : BackInTimeDebuggerService {
            override val stateFlow = MutableStateFlow(State())
            override fun restartServer(port: Int) {}
            override fun sendEvent(sessionId: String, event: BackInTimeDebuggerEvent) {}
        }
    }
}
