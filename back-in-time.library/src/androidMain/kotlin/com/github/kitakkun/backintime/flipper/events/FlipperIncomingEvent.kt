package com.github.kitakkun.backintime.flipper.events

/**
 * Desktop app -> Mobile app
 */
sealed interface FlipperIncomingEvent {
    data class CheckInstanceAlive(
        val instanceUUIDs: List<String>,
    ) : FlipperIncomingEvent {
        companion object {
            const val EVENT_NAME = "refreshInstanceAliveStatus"
        }

        data class Response(
            val isAlive: Map<String, Boolean>,
        )
    }

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
