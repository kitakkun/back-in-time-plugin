@file:OptIn(ExperimentalJsExport::class)
@file:Suppress("NON_EXPORTABLE_TYPE")

package com.kitakkun.backintime.core.websocket.event

@JsExport
fun createCheckInstanceAliveEvent(instanceUUIDs: Array<String>) = BackInTimeDebuggerEvent.CheckInstanceAlive(instanceUUIDs = instanceUUIDs.toList())
