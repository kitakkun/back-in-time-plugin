package com.kitakkun.backintime.core.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.kitakkun.backintime.core.runtime.backInTimeJson
import com.kitakkun.backintime.core.runtime.connector.BackInTimeWebSocketConnector
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BackInTimeFlipperConnector(
    private val connection: FlipperConnection,
) : BackInTimeWebSocketConnector {
    override suspend fun connect(): Flow<BackInTimeDebuggerEvent> = channelFlow {
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

    override suspend fun sendEventToDebugger(event: BackInTimeDebugServiceEvent) {
        val eventKey = when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> "register"
            is BackInTimeDebugServiceEvent.RegisterRelationship -> "registerRelationship"
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> "notifyMethodCall"
            is BackInTimeDebugServiceEvent.NotifyValueChange -> "notifyValueChange"
            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> "checkInstanceAliveResult"
            else -> null
        } ?: return
        connection.send(eventKey, FlipperObject(Json.encodeToString(event)))
    }

    override suspend fun close() {
        // no-op
    }

    override suspend fun awaitCloseSession() {
        // no-op
    }
}
