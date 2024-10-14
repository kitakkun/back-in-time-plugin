package io.github.kitakkun.backintime.websocket.event

import kotlinx.serialization.Serializable

/**
 * events from debugger to debugService
 */
@Serializable
sealed class BackInTimeDebuggerEvent {
    @Serializable
    data object Ping : BackInTimeDebuggerEvent()

    @Serializable
    data class CheckInstanceAlive(
        val instanceUUIDs: List<String>,
    ) : BackInTimeDebuggerEvent()

    @Serializable
    data class ForceSetPropertyValue(
        val instanceUUID: String,
        val ownerClassFqName: String,
        val propertyName: String,
        val value: String,
    ) : BackInTimeDebuggerEvent()

    @Serializable
    data class Error(
        val message: String,
    ) : BackInTimeDebuggerEvent()
}
