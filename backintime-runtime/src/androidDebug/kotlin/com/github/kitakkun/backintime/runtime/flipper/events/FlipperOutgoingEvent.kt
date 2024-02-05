package com.github.kitakkun.backintime.runtime.flipper.events

import com.github.kitakkun.backintime.runtime.PropertyInfo
import kotlinx.serialization.Serializable

/**
 * Mobile app -> Desktop app
 */
sealed interface FlipperOutgoingEvent {
    @Serializable
    data class RegisterInstance(
        val instanceUUID: String,
        val className: String,
        val superClassName: String,
        val properties: List<PropertyInfo>,
        val registeredAt: Long,
    ) : FlipperOutgoingEvent {
        companion object {
            const val EVENT_NAME = "register"
        }
    }

    @Serializable
    data class NotifyValueChange(
        val instanceUUID: String,
        val propertyName: String,
        val value: String,
        val methodCallUUID: String,
    ) : FlipperOutgoingEvent {
        companion object {
            const val EVENT_NAME = "notifyValueChange"
        }
    }

    @Serializable
    data class NotifyMethodCall(
        val instanceUUID: String,
        val methodName: String,
        val methodCallUUID: String,
        val calledAt: Long,
    ) : FlipperOutgoingEvent {
        companion object {
            const val EVENT_NAME = "notifyMethodCall"
        }
    }

    @Serializable
    data class RegisterRelationship(
        val parentUUID: String,
        val childUUID: String,
    ) : FlipperOutgoingEvent {
        companion object {
            const val EVENT_NAME = "registerRelationship"
        }
    }
}
