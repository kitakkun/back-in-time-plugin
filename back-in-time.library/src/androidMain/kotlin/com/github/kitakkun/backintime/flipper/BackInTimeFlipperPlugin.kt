package com.github.kitakkun.backintime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class BackInTimeFlipperPlugin : FlipperPlugin, CoroutineScope by MainScope() {
    private var connection: FlipperConnection? = null
    private val service: BackInTimeDebugService = BackInTimeDebugService
    override fun getId() = "back-in-time"
    override fun runInBackground() = false

    init {
        launch {
            service.valueChangeFlow.collect {
                connection?.send(
                    "valueChanged",
                    FlipperObject.Builder()
                        .put("instanceUUID", it.instanceUUID)
                        .put("paramKey", it.propertyName)
                        .put("value", it.value)
                        .build()
                )
            }
        }
    }

    override fun onConnect(connection: FlipperConnection?) {
        this.connection = connection
        this.connection?.apply {
            receive("forceUpdateState") { params, responder ->
                val instanceUUID = params.getString("instanceUUID")
                val propertyName = params.getString("propertyName")
                val rawValue = params.getString("value")
                service.manipulate(instanceUUID, propertyName, rawValue)
            }
        }
    }

    override fun onDisconnect() {
        connection = null
    }
}
