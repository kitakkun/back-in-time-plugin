package com.github.kitakkun.back_in_time

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperObject
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.back_in_time.annotations.BackInTimeDebugService
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
                        .put("instanceId", it.instanceId)
                        .put("paramKey", it.paramKey)
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
                val instanceId = params.getInt("instanceId")
                val paramKey = params.getString("paramKey")
                val value = params.getString("value")
                service.manipulate(instanceId, paramKey, value)
            }
        }
    }

    override fun onDisconnect() {
        connection = null
    }
}
