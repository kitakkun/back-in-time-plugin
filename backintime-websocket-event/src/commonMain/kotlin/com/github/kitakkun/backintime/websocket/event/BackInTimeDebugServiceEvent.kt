@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package com.github.kitakkun.backintime.websocket.event

import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo
import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

/**
 * events from debugService to debugger
 */
@Serializable
sealed class BackInTimeDebugServiceEvent {
    @Serializable
    data object Ping : BackInTimeDebugServiceEvent()

    @Serializable
    data class RegisterInstance(
        val instanceUUID: String,
        val className: String,
        val superClassName: String,
        val properties: List<PropertyInfo>,
        val registeredAt: Long,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class NotifyValueChange(
        val instanceUUID: String,
        val propertyFqName: String,
        val value: String,
        val methodCallUUID: String,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class NotifyMethodCall(
        val instanceUUID: String,
        val methodName: String,
        val methodCallUUID: String,
        val calledAt: Long,
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
}
