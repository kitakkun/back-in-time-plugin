package com.kitakkun.jetwhale.plugins.backintime.protocol

import com.kitakkun.jetwhale.protocol.messaging.JetWhaleRequest
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Requests the host issues against the agent, with their replies.
 *
 * Both are requests rather than events: the inspector needs to know whether an instance is still
 * reachable before offering to rewind it, and a rewind that silently did nothing (because the
 * instance was garbage collected in the meantime) would be indistinguishable from a successful one.
 */

/** Asks which of [instanceUUIDs] the agent still holds a live reference to. */
@SerialName("backintime/check_instance_alive")
@Serializable
public data class CheckInstanceAlive(
    val instanceUUIDs: List<String>,
) : JetWhaleRequest<InstanceAliveness>

/** Reply to [CheckInstanceAlive]: one entry per requested UUID. */
@SerialName("backintime/instance_aliveness")
@Serializable
public data class InstanceAliveness(
    val isAlive: Map<String, Boolean>,
)

/** Rewinds a single property of a live instance to [jsonValue]. */
@SerialName("backintime/force_set_property_value")
@Serializable
public data class ForceSetPropertyValue(
    val targetInstanceId: String,
    val propertySignature: String,
    val jsonValue: String,
) : JetWhaleRequest<ForceSetPropertyValueResult>

/** Reply to [ForceSetPropertyValue]. [applied] is `false` when the instance is no longer alive. */
@SerialName("backintime/force_set_property_value_result")
@Serializable
public data class ForceSetPropertyValueResult(
    val applied: Boolean,
)
