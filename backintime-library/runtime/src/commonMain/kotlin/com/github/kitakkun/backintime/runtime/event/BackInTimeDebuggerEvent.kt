package com.github.kitakkun.backintime.runtime.event

import kotlinx.serialization.Serializable

/**
 * events from debugger to debugService
 */
@Serializable
sealed interface BackInTimeDebuggerEvent {
    @Serializable
    data class CheckInstanceAlive(
        val instanceUUIDs: List<String>,
    ) : BackInTimeDebuggerEvent {
        @Serializable
        data class Response(
            val isAlive: Map<String, Boolean>,
        )
    }

    @Serializable
    data class ForceSetPropertyValue(
        val instanceUUID: String,
        val propertyName: String,
        val value: String,
        val valueType: String,
    ) : BackInTimeDebuggerEvent
}
