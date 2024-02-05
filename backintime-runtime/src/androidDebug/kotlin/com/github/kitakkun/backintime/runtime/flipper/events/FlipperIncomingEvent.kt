package com.github.kitakkun.backintime.runtime.flipper.events

import kotlinx.serialization.Serializable

/**
 * Desktop app -> Mobile app
 */
sealed interface FlipperIncomingEvent {
    @Serializable
    data class CheckInstanceAlive(
        val instanceUUIDs: List<String>,
    ) : FlipperIncomingEvent {
        companion object {
            const val EVENT_NAME = "refreshInstanceAliveStatus"
        }

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
    ) : FlipperIncomingEvent {
        companion object {
            const val EVENT_NAME = "forceSetPropertyValue"
        }
    }
}
