package com.github.kitakkun.backintime.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("unused")
class BackInTimeFlipperPlugin : FlipperPlugin, CoroutineScope by MainScope() {
    private var connection: FlipperConnection? = null
    private val service: BackInTimeDebugService = BackInTimeDebugService
    private val json: Json = Json { encodeDefaults = true }
    private var observeOutgoingEventsJob: Job? = null

    override fun getId() = "back-in-time"
    override fun runInBackground() = true

    override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
        this.connection?.observeIncomingEvents()
        observeOutgoingEventsJob?.cancel()
        observeOutgoingEventsJob = observeOutgoingEvents()
        launch {
            // Flipper側でイベントを受信できない場合があるので少し遅らせる
            delay(1000)
            service.start()
        }
    }

    override fun onDisconnect() {
        service.suspend()
        connection = null
        observeOutgoingEventsJob?.cancel()
        observeOutgoingEventsJob = null
    }

    private fun observeOutgoingEvents() = launch {
        launch {
            service.serviceEventFlow.collect { event ->
                val eventKey = when (event) {
                    is BackInTimeDebugServiceEvent.RegisterInstance -> "register"
                    is BackInTimeDebugServiceEvent.RegisterRelationship -> "registerRelationship"
                    is BackInTimeDebugServiceEvent.NotifyMethodCall -> "notifyMethodCall"
                    is BackInTimeDebugServiceEvent.NotifyValueChange -> "notifyValueChange"
                    is BackInTimeDebugServiceEvent.Ping -> "ping"
                }
                connection?.send(eventKey, FlipperObject(json.encodeToString(event)))
            }
        }

        launch {
            service.internalErrorFlow.collect { throwable ->
                connection?.reportError(throwable)
            }
        }
    }

    private fun FlipperConnection.observeIncomingEvents() {
        receive("forceSetPropertyValue") { params, responder ->
            val event = json.decodeFromString<BackInTimeDebuggerEvent.ForceSetPropertyValue>(params.toJsonString())
            val (instanceUUID, propertyName, value) = event
            service.forceSetValue(instanceUUID, propertyName, value)
            /**
             *  error is handled by [BackInTimeDebugService.internalErrorFlow]
             */
            responder.success()
        }

        receive("refreshInstanceAliveStatus") { params, responder ->
            val event = json.decodeFromString<BackInTimeDebuggerEvent.CheckInstanceAlive>(params.toJsonString())
            val response = BackInTimeDebuggerEvent.CheckInstanceAlive.Response(event.instanceUUIDs.associateWith { service.checkIfInstanceIsAlive(it) })
            responder.success(FlipperObject(json.encodeToString(response)))
        }
    }
}
