package com.github.kitakkun.backintime.flipper.events

import com.github.kitakkun.backintime.runtime.PropertyInfo

/**
 * Mobile app -> Desktop app
 */
sealed interface FlipperOutgoingEvent {
    data class RegisterInstance(
        val instanceUUID: String,
        val instanceType: String,
        val properties: List<PropertyInfo>,
        val registeredAt: Long,
    ) : FlipperOutgoingEvent {
        companion object {
            const val EVENT_NAME = "register"
        }
    }

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
}
