package com.github.kitakkun.backintime.runtime.event

import com.github.kitakkun.backintime.runtime.PropertyInfo
import kotlinx.serialization.Serializable

/**
 * events from debugService to debugger
 */
@Serializable
sealed interface BackInTimeDebugServiceEvent {
    @Serializable
    data class RegisterInstance(
        val instanceUUID: String,
        val className: String,
        val superClassName: String,
        val properties: List<PropertyInfo>,
        val registeredAt: Long,
    ) : BackInTimeDebugServiceEvent

    @Serializable
    data class NotifyValueChange(
        val instanceUUID: String,
        val propertyName: String,
        val value: String,
        val methodCallUUID: String,
    ) : BackInTimeDebugServiceEvent

    @Serializable
    data class NotifyMethodCall(
        val instanceUUID: String,
        val methodName: String,
        val methodCallUUID: String,
        val calledAt: Long,
    ) : BackInTimeDebugServiceEvent

    @Serializable
    data class RegisterRelationship(
        val parentUUID: String,
        val childUUID: String,
    ) : BackInTimeDebugServiceEvent
}
