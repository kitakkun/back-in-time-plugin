package io.github.kitakkun.backintime.core.websocket.event

import io.github.kitakkun.backintime.core.websocket.event.model.PropertyInfo
import kotlinx.serialization.Serializable

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
        val ownerClassFqName: String,
        val propertyName: String,
        val value: String,
        val methodCallUUID: String,
    ) : BackInTimeDebugServiceEvent()

    @Serializable
    data class NotifyMethodCall(
        val instanceUUID: String,
        val ownerClassFqName: String,
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
