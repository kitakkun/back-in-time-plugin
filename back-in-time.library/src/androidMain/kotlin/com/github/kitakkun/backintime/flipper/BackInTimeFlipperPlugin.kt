package com.github.kitakkun.backintime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.flipper.events.FlipperIncomingEvent
import com.github.kitakkun.backintime.flipper.events.FlipperOutgoingEvent
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

abstract class BackInTimeFlipperPlugin : FlipperPlugin, CoroutineScope by MainScope() {
    private var connection: FlipperConnection? = null
    private val service: BackInTimeDebugService = BackInTimeDebugService
    private val json: Json = Json { encodeDefaults = true }
    private var observeOutgoingEventsJob: Job? = null

    final override fun getId() = "back-in-time"
    final override fun runInBackground() = true

    final override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
        this.connection?.observeIncomingEvents()
        observeOutgoingEventsJob?.cancel()
        observeOutgoingEventsJob = observeOutgoingEvents()
    }

    final override fun onDisconnect() {
        connection = null
        observeOutgoingEventsJob?.cancel()
        observeOutgoingEventsJob = null
    }

    abstract fun serializeValue(value: Any?, valueType: String): String
    abstract fun deserializeValue(value: String, valueType: String): Any?

    private fun observeOutgoingEvents() = launch {
        launch {
            service.registeredInstanceFlow.collect { instanceInfo ->
                val event = FlipperOutgoingEvent.RegisterInstance(
                    instanceUUID = instanceInfo.uuid,
                    instanceType = instanceInfo.type,
                    properties = instanceInfo.properties,
                    registeredAt = instanceInfo.registeredAt,
                )
                connection?.send(FlipperOutgoingEvent.RegisterInstance.EVENT_NAME, FlipperObject(json.encodeToString(event)))
            }
        }

        launch {
            service.notifyMethodCallFlow.collect { methodCallInfo ->
                val event = FlipperOutgoingEvent.NotifyMethodCall(
                    instanceUUID = methodCallInfo.instanceUUID,
                    methodName = methodCallInfo.methodName,
                    methodCallUUID = methodCallInfo.methodCallUUID,
                    calledAt = methodCallInfo.calledAt,
                )
                connection?.send(FlipperOutgoingEvent.NotifyMethodCall.EVENT_NAME, FlipperObject(json.encodeToString(event)))
            }
        }

        launch {
            service.notifyValueChangeFlow.collect { valueChangeInfo ->
                val event = FlipperOutgoingEvent.NotifyValueChange(
                    instanceUUID = valueChangeInfo.instanceUUID,
                    propertyName = valueChangeInfo.propertyName,
                    value = valueChangeInfo.value,
                    methodCallUUID = valueChangeInfo.methodCallUUID,
                )
                connection?.send(FlipperOutgoingEvent.NotifyValueChange.EVENT_NAME, FlipperObject(json.encodeToString(event)))
            }
        }
    }

    private fun FlipperConnection.observeIncomingEvents() {
        receive(FlipperIncomingEvent.ForceSetPropertyValue.EVENT_NAME) { params, responder ->
            val event = json.decodeFromString<FlipperIncomingEvent.ForceSetPropertyValue>(params.toJsonString())
            with(event) {
                service.manipulate(instanceUUID, propertyName, value)
            }
            // FIXME: this should be called after the value is actually changed
            //  error handling is also necessary
            responder.success()
        }

        receive(FlipperIncomingEvent.CheckInstanceAlive.EVENT_NAME) { params, responder ->
            val event = json.decodeFromString<FlipperIncomingEvent.CheckInstanceAlive>(params.toJsonString())
            val response = FlipperIncomingEvent.CheckInstanceAlive.Response(event.instanceUUIDs.associateWith { service.checkIfInstanceIsAlive(it) })
            responder.success(FlipperObject(json.encodeToString(response)))
        }
    }
}
