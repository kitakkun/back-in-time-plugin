package com.kitakkun.backintime.tooling.model

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
sealed class InstanceEventData {
    @OptIn(ExperimentalUuidApi::class)
    val eventId: String = Uuid.random().toString()

    abstract val instanceId: String
    abstract val time: Long

    @Serializable
    data class Register(
        override val instanceId: String,
        override val time: Long,
        val classInfo: ClassInfo,
    ) : InstanceEventData()

    @Serializable
    data class MethodInvocation(
        override val instanceId: String,
        override val time: Long,
        val callId: String,
        val methodFqName: String,
    ) : InstanceEventData()

    @Serializable
    data class StateChange(
        override val instanceId: String,
        override val time: Long,
        val callId: String,
        val propertyFqName: String,
        val jsonValue: String,
    ) : InstanceEventData()

    @Serializable
    data class Unregister(
        override val instanceId: String,
        override val time: Long,
    ) : InstanceEventData()
}

