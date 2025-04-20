package com.kitakkun.backintime.tooling.model

import com.benasher44.uuid.uuid4
import kotlinx.serialization.Serializable

/**
 * Database entity types corresponding to [com.kitakkun.backintime.core.websocket.event.BackInTimeWebSocketEvent]
 */
@Serializable
sealed class EventEntity {
    val eventId: String = uuid4().toString()

    abstract val sessionId: String
    abstract val instanceId: String?
    abstract val time: Long

    @Serializable
    sealed class System : EventEntity() {
        override val instanceId: String? get() = null

        @Serializable
        data class CheckInstanceAlive(
            override val sessionId: String,
            override val time: Long,
        ) : System()

        @Serializable
        data class CheckInstanceAliveResult(
            override val sessionId: String,
            override val time: Long,
            val isAlive: Map<String, Boolean>,
        ) : System()

        @Serializable
        data class DebuggerError(
            override val sessionId: String,
            override val time: Long,
            val message: String,
        ) : System()

        @Serializable
        data class AppError(
            override val sessionId: String,
            override val time: Long,
            val message: String,
        ) : System()
    }

    @Serializable
    sealed class Instance : EventEntity() {
        @Serializable
        data class Register(
            override val sessionId: String,
            override val instanceId: String,
            override val time: Long,
            val classInfo: ClassInfo,
        ) : Instance()

        @Serializable
        data class MethodInvocation(
            override val sessionId: String,
            override val instanceId: String,
            override val time: Long,
            val callId: String,
            val methodSignature: String,
        ) : Instance()

        @Serializable
        data class StateChange(
            override val sessionId: String,
            override val instanceId: String,
            override val time: Long,
            val callId: String,
            val propertySignature: String,
            val newValueAsJson: String,
        ) : Instance()

        @Serializable
        data class Unregister(
            override val sessionId: String,
            override val instanceId: String,
            override val time: Long,
        ) : Instance()

        @Serializable
        data class NewDependency(
            override val sessionId: String,
            override val instanceId: String,
            override val time: Long,
            val dependencyInstanceId: String,
        ) : Instance()

        @Serializable
        data class BackInTime(
            override val sessionId: String,
            override val instanceId: String,
            override val time: Long,
            val jsonValues: Map<String, String>,
            val destinationPointEventId: String?,
        ) : Instance()
    }
}