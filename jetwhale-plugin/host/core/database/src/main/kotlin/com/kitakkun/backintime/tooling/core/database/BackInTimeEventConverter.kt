package com.kitakkun.backintime.tooling.core.database

import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkun.backintime.tooling.model.PropertyInfo
import com.kitakkun.jetwhale.plugins.backintime.protocol.AgentErrorOccurred
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceRegistered
import com.kitakkun.jetwhale.plugins.backintime.protocol.InstanceStateChanged
import com.kitakkun.jetwhale.plugins.backintime.protocol.MethodInvoked
import com.kitakkun.jetwhale.plugins.backintime.protocol.RelationshipRegistered

/**
 * Turns the messages the agent sends over JetWhale into the rows the debugger UI reads back.
 *
 * The host-issued side of the protocol (`CheckInstanceAlive` / `ForceSetPropertyValue`) is recorded
 * by the plugin itself when it issues the request, since only the caller knows the whole rewind —
 * the wire carries one property assignment per request.
 */
class BackInTimeEventConverter {
    fun convertToEntity(sessionId: String, event: InstanceRegistered): EventEntity = EventEntity.Instance.Register(
        sessionId = sessionId,
        instanceId = event.instanceUUID,
        time = event.time,
        classInfo = ClassInfo(
            classSignature = event.classSignature,
            superClassSignature = event.superClassSignature,
            properties = event.properties.map(PropertyInfo::fromString),
        ),
    )

    fun convertToEntity(sessionId: String, event: MethodInvoked): EventEntity = EventEntity.Instance.MethodInvocation(
        sessionId = sessionId,
        instanceId = event.instanceUUID,
        time = event.time,
        methodSignature = event.methodSignature,
        callId = event.methodCallUUID,
    )

    fun convertToEntity(sessionId: String, event: InstanceStateChanged): EventEntity = EventEntity.Instance.StateChange(
        sessionId = sessionId,
        instanceId = event.instanceUUID,
        time = event.time,
        propertySignature = event.propertySignature,
        newValueAsJson = event.jsonValue,
        callId = event.methodCallUUID,
    )

    fun convertToEntity(sessionId: String, event: RelationshipRegistered): EventEntity = EventEntity.Instance.NewDependency(
        sessionId = sessionId,
        instanceId = event.parentUUID,
        time = event.time,
        dependencyInstanceId = event.childUUID,
    )

    fun convertToEntity(sessionId: String, event: AgentErrorOccurred): EventEntity = EventEntity.System.AppError(
        sessionId = sessionId,
        time = event.time,
        message = event.message,
    )
}
