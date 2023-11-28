package com.github.kitakkun.backintime.flipper

import android.util.Log
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.flipper.events.FlipperIncomingEvent
import com.github.kitakkun.backintime.flipper.events.FlipperOutgoingEvent
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class BackInTimeFlipperPlugin : FlipperPlugin, CoroutineScope by MainScope() {
    private var connection: FlipperConnection? = null
    private val service: BackInTimeDebugService = BackInTimeDebugService
    private val gson = Gson()

    final override fun getId() = "back-in-time"
    final override fun runInBackground() = true

    init {
        observeOutgoingEvents()
    }

    final override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
        this.connection?.observeIncomingEvents()
    }

    final override fun onDisconnect() {
        connection = null
    }

    abstract fun serializeValue(value: Any?, valueType: String): String
    abstract fun deserializeValue(value: String, valueType: String): Any?

    private fun observeOutgoingEvents() {
        launch {
            // wait until the connection is established
            // temporary fix: this may not work property if the connection is disconnected and reconnected
            while (connection == null);
            service.registeredInstanceFlow.collect { instanceInfo ->
                val event = FlipperOutgoingEvent.RegisterInstance(
                    instanceUUID = instanceInfo.uuid,
                    instanceType = instanceInfo.type,
                    properties = instanceInfo.properties,
                    registeredAt = instanceInfo.registeredAt,
                )
                connection?.send(FlipperOutgoingEvent.RegisterInstance.EVENT_NAME, FlipperObject(gson.toJson(event)))
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
                connection?.send(FlipperOutgoingEvent.NotifyMethodCall.EVENT_NAME, FlipperObject(gson.toJson(event)))
            }
        }

        launch {
            service.notifyValueChangeFlow.collect { valueChangeInfo ->
                val event = FlipperOutgoingEvent.NotifyValueChange(
                    instanceUUID = valueChangeInfo.instanceUUID,
                    propertyName = valueChangeInfo.propertyName,
                    value = serializeValue(valueChangeInfo.value, valueChangeInfo.valueType),
                    methodCallUUID = valueChangeInfo.methodCallUUID,
                )
                connection?.send(FlipperOutgoingEvent.NotifyValueChange.EVENT_NAME, FlipperObject(gson.toJson(event)))
            }
        }
    }

    private fun FlipperConnection.observeIncomingEvents() {
        receive(FlipperIncomingEvent.ForceSetPropertyValue.EVENT_NAME) { params, responder ->
            val event = gson.fromJson(params.toJsonString(), FlipperIncomingEvent.ForceSetPropertyValue::class.java)
            with(event) {
                service.manipulate(instanceUUID, propertyName, deserializeValue(value, valueType))
            }
            // FIXME: this should be called after the value is actually changed
            //  error handling is also necessary
            responder.success()
        }

        receive(FlipperIncomingEvent.CheckInstanceAlive.EVENT_NAME) { params, responder ->
            val event = gson.fromJson(params.toJsonString(), FlipperIncomingEvent.CheckInstanceAlive::class.java)
            val response = FlipperIncomingEvent.CheckInstanceAlive.Response(event.instanceUUIDs.associateWith { service.checkIfInstanceIsAlive(it) })
            responder.success(FlipperObject(gson.toJson(response)))
        }
    }
}
