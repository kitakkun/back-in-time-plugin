package com.kitakkun.jetwhale.plugins.backintime.protocol

import com.kitakkun.jetwhale.protocol.messaging.JetWhaleEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Fire-and-forget notifications the agent (the app being debugged) pushes to the host.
 *
 * Each carries the wall-clock time the agent observed the event at, because the host records them
 * into a database whose ordering must reflect what happened in the app rather than when the host
 * happened to receive it — a reconnect flushes buffered events long after the fact.
 */

/** A new debuggable instance was created and registered with the runtime. */
@SerialName("backintime/instance_registered")
@Serializable
public data class InstanceRegistered(
    val instanceUUID: String,
    val classSignature: String,
    val superClassSignature: String,
    val properties: List<String>,
    val time: Long,
) : JetWhaleEvent

/** A debuggable property of a registered instance changed value. */
@SerialName("backintime/instance_state_changed")
@Serializable
public data class InstanceStateChanged(
    val instanceUUID: String,
    val propertySignature: String,
    val jsonValue: String,
    val methodCallUUID: String,
    val time: Long,
) : JetWhaleEvent

/** A method of a registered instance was entered; state changes reference it by [methodCallUUID]. */
@SerialName("backintime/method_invoked")
@Serializable
public data class MethodInvoked(
    val instanceUUID: String,
    val methodSignature: String,
    val methodCallUUID: String,
    val time: Long,
) : JetWhaleEvent

/** A registered instance holds another registered instance as one of its properties. */
@SerialName("backintime/relationship_registered")
@Serializable
public data class RelationshipRegistered(
    val parentUUID: String,
    val childUUID: String,
    val time: Long,
) : JetWhaleEvent

/** The back-in-time runtime failed while processing an instance event. */
@SerialName("backintime/agent_error")
@Serializable
public data class AgentErrorOccurred(
    val message: String,
    val time: Long,
) : JetWhaleEvent
