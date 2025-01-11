@file:Suppress("UNUSED")
@file:JsModule("flipper-plugin")
@file:JsNonModule

package com.kitakkun.backintime.tooling.flipper

import kotlin.js.Promise

external interface PluginClient<Events, Methods> {
    val appId: String
    val appName: String
    val isConnected: Boolean

    fun onConnect(cb: () -> Unit): () -> Unit
    fun onDisconnect(cb: () -> Unit): () -> Unit
    fun onMessage(event: String, callback: (params: dynamic) -> Unit): () -> Unit
    fun send(method: String, params: dynamic): Promise<dynamic>
    fun supportsMethod(method: String): Promise<Boolean>
}
