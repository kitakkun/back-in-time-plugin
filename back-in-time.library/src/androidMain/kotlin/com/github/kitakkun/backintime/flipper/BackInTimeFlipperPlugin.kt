package com.github.kitakkun.backintime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.flipper.events.FlipperIncomingEvent
import com.github.kitakkun.backintime.flipper.events.FlipperOutgoingEvent
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
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
            service.registeredInstanceFlow.collect { instanceInfo ->
                val event = FlipperOutgoingEvent.RegisterInstance(
                    instanceInfo.uuid,
                    instanceInfo.type,
                    instanceInfo.properties,
                    instanceInfo.registeredAt,
                )
                connection?.send(FlipperOutgoingEvent.RegisterInstance.EVENT_NAME, FlipperObject(gson.toJson(event)))
            }
        }

        launch {
            service.valueChangeFlow.collect { valueChangeData ->
                val event = FlipperOutgoingEvent.NotifyValueChange(
                    valueChangeData.instanceUUID,
                    valueChangeData.propertyName,
                    serializeValue(valueChangeData.value, valueChangeData.valueType),
                    valueChangeData.methodCallInfo.callUuid,
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
