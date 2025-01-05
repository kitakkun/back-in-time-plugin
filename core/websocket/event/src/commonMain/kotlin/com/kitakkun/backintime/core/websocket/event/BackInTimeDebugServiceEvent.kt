@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package com.kitakkun.backintime.core.websocket.event

import com.kitakkun.backintime.tooling.model.PropertyInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * events from debugService to debugger
 */
@Serializable
sealed class BackInTimeDebugServiceEvent : BackInTimeWebSocketEvent {
    @Serializable
    data object Ping : BackInTimeDebugServiceEvent()

    @Serializable
    data class RegisterInstance(
        val instanceUUID: String,
        val classSignature: String,
        val superClassSignature: String,
        val properties: List<PropertyInfo>,
        val registeredAt: Int,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class NotifyValueChange(
        val instanceUUID: String,
        val propertySignature: String,
        val value: String,
        val methodCallUUID: String,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class NotifyMethodCall(
        val instanceUUID: String,
        val methodSignature: String,
        val methodCallUUID: String,
        val calledAt: Int,
    ) : BackInTimeDebugServiceEvent()

    data class RegisterRelationship(
        val parentUUID: String,
        val childUUID: String,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class CheckInstanceAliveResult(
        val isAlive: Map<String, Boolean>,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class Error(
        val message: String,
    ) : BackInTimeDebugServiceEvent()

    companion object {
        // fixme: same as backInTimeJson defined in the runtime library.
        private val backInTimeJson = Json {
            encodeDefaults = true
            explicitNulls = true
        }

        fun fromJsonString(jsonString: String): BackInTimeDebugServiceEvent {
            return backInTimeJson.decodeFromString<BackInTimeDebugServiceEvent>(jsonString)
        }
    }
}
