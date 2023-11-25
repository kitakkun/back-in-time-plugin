package com.github.kitakkun.backintime.flipper

import android.util.Log
import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

abstract class BackInTimeFlipperPlugin : FlipperPlugin, CoroutineScope by MainScope() {
    private var connection: FlipperConnection? = null
    private val service: BackInTimeDebugService = BackInTimeDebugService
    final override fun getId() = "back-in-time"
    final override fun runInBackground() = true

    init {
        launch {
            service.valueChangeFlow.collect { valueChangeData ->
                val stringifiedValue = serializeValue(valueChangeData.value, valueChangeData.valueType)
                val eventObject = FlipperObject.Builder()
                    .put("instanceUUID", valueChangeData.instanceUUID)
                    .put("propertyName", valueChangeData.propertyName)
                    .put("value", stringifiedValue)
                    .put("valueType", valueChangeData.valueType)
                    .build()
                Log.d("BackInTimeFlipperPlugin", "valueChanged: $eventObject")
                connection?.send(
                    "valueChanged",
                    eventObject
                )
            }
        }
    }

    final override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
        this.connection?.apply {
            receive("forceUpdateState") { params, responder ->
                val instanceUUID = params.getString("instanceUUID")
                val propertyName = params.getString("propertyName")
                val rawValue = params.getString("value")
                val valueType = params.getString("valueType")
                val value = deserializeValue(rawValue, valueType)
                Log.d("BackInTimeFlipperPlugin", "forceUpdateState: $instanceUUID, $propertyName, $value")
                service.manipulate(instanceUUID, propertyName, value)
            }
        }
    }

    final override fun onDisconnect() {
        connection = null
    }

    abstract fun serializeValue(value: Any?, valueType: String): String
    abstract fun deserializeValue(value: String, valueType: String): Any?
}
