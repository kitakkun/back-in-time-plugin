package com.github.kitakkun.backintime.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.github.kitakkun.backintime.runtime.backInTimeJson
import com.github.kitakkun.backintime.runtime.connector.BackInTimeConnector
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackInTimeFlipperConnector(
    private val connection: FlipperConnection,
) : BackInTimeConnector {
    override val connectedFlow: Flow<Boolean> = flow { emit(true) }
    override val connected: Boolean get() = true

    override fun connect() {
        // no-op
    }

    override fun disconnect() {
        // no-op
    }

    override fun sendEvent(event: BackInTimeDebugServiceEvent) {
        val eventKey = when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> "register"
            is BackInTimeDebugServiceEvent.RegisterRelationship -> "registerRelationship"
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> "notifyMethodCall"
            is BackInTimeDebugServiceEvent.NotifyValueChange -> "notifyValueChange"
            else -> null
        } ?: return
        connection.send(eventKey, FlipperObject(Json.encodeToString(event)))
    }

    override fun receiveEventAsFlow() = channelFlow {
        connection.receive("forceSetPropertyValue") { params, responder ->
            val event = backInTimeJson.decodeFromString<BackInTimeDebuggerEvent.ForceSetPropertyValue>(params.toJsonString())
            launch {
                send(event)
            }
            responder.success()
        }

        connection.receive("refreshInstanceAliveStatus") { params, responder ->
            val event = backInTimeJson.decodeFromString<BackInTimeDebuggerEvent.CheckInstanceAlive>(params.toJsonString())
            launch {
                send(event)
            }
            responder.success()
        }

        awaitClose()
    }
}
