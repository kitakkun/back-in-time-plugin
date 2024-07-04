@file:OptIn(ExperimentalJsExport::class)
@file:Suppress("NON_EXPORTABLE_TYPE")

package com.github.kitakkun.backintime.websocket.event

@JsExport
fun createCheckInstanceAliveEvent(instanceUUIDs: Array<String>) = BackInTimeDebuggerEvent.CheckInstanceAlive(instanceUUIDs = instanceUUIDs.toList())
