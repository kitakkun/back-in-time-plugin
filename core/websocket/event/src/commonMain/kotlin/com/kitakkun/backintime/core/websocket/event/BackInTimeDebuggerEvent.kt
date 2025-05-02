@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package com.kitakkun.backintime.core.websocket.event

import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * events from debugger to debugService
 */
@JsExport
@Serializable
sealed class BackInTimeDebuggerEvent : BackInTimeWebSocketEvent {
    @Serializable
    data class Ping(
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ) : BackInTimeDebuggerEvent()

    @Serializable
    data class CheckInstanceAlive(
        val instanceUUIDs: List<String>,
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ) : BackInTimeDebuggerEvent()

    @Serializable
    data class ForceSetPropertyValue(
        val targetInstanceId: String,
        val propertySignature: String,
        val jsonValue: String,
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ) : BackInTimeDebuggerEvent()

    @Serializable
    data class Error(
        val message: String,
        override val time: Long = Clock.System.now().toEpochMilliseconds(),
    ) : BackInTimeDebuggerEvent()

    companion object {
        // fixme: same as backInTimeJson defined in the runtime library.
        private val backInTimeJson = Json {
            encodeDefaults = true
            explicitNulls = true
        }

        fun toJsonString(event: BackInTimeDebuggerEvent): String {
            return backInTimeJson.encodeToString(event)
        }
    }
}
