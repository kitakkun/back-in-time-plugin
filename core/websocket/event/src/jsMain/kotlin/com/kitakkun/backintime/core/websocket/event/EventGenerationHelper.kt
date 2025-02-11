@file:OptIn(ExperimentalJsExport::class)
@file:Suppress("NON_EXPORTABLE_TYPE")

package com.kitakkun.backintime.core.websocket.event

import kotlinx.datetime.Clock

@JsExport
fun createCheckInstanceAliveEvent(instanceUUIDs: Array<String>): BackInTimeDebuggerEvent.CheckInstanceAlive {
    return BackInTimeDebuggerEvent.CheckInstanceAlive(instanceUUIDs = instanceUUIDs.toList(), time = Clock.System.now().epochSeconds.toInt())
}
