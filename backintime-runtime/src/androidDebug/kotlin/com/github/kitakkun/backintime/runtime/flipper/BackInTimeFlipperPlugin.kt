package com.github.kitakkun.backintime.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.flipper.events.FlipperIncomingEvent
import com.github.kitakkun.backintime.runtime.flipper.events.FlipperOutgoingEvent
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
            service.registeredInstanceFlow.collect { instanceInfo ->
                val event = FlipperOutgoingEvent.RegisterInstance(
                    instanceUUID = instanceInfo.uuid,
                    className = instanceInfo.type,
                    superClassName = instanceInfo.superType,
                    properties = instanceInfo.properties,
                    registeredAt = instanceInfo.registeredAt,
                )
                connection?.send(FlipperOutgoingEvent.RegisterInstance.EVENT_NAME, FlipperObject(json.encodeToString(event)))
            }
        }

        launch {
            service.registerRelationshipFlow.collect { relationshipInfo ->
                val event = FlipperOutgoingEvent.RegisterRelationship(
                    parentUUID = relationshipInfo.from,
                    childUUID = relationshipInfo.to,
                )
                connection?.send(FlipperOutgoingEvent.RegisterRelationship.EVENT_NAME, FlipperObject(json.encodeToString(event)))
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

        launch {
            service.internalErrorFlow.collect { throwable ->
                connection?.reportError(throwable)
            }
        }
    }

    private fun FlipperConnection.observeIncomingEvents() {
        receive(FlipperIncomingEvent.ForceSetPropertyValue.EVENT_NAME) { params, responder ->
            val event = json.decodeFromString<FlipperIncomingEvent.ForceSetPropertyValue>(params.toJsonString())
            val (instanceUUID, propertyName, value) = event
            service.forceSetValue(instanceUUID, propertyName, value)
            /**
             *  error is handled by [BackInTimeDebugService.internalErrorFlow]
             */
            responder.success()
        }

        receive(FlipperIncomingEvent.CheckInstanceAlive.EVENT_NAME) { params, responder ->
            val event = json.decodeFromString<FlipperIncomingEvent.CheckInstanceAlive>(params.toJsonString())
            val response = FlipperIncomingEvent.CheckInstanceAlive.Response(event.instanceUUIDs.associateWith { service.checkIfInstanceIsAlive(it) })
            responder.success(FlipperObject(json.encodeToString(response)))
        }
    }
}