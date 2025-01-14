package com.kitakkun.backintime.tooling.idea.service

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeWebSocketEvent
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.InstanceEventData
import com.kitakkun.backintime.tooling.model.PropertyInfo

class BackInTimeEventConverter {
    fun convert(event: BackInTimeWebSocketEvent): InstanceEventData? {
        return when (event) {
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> {
                InstanceEventData.MethodInvocation(
                    instanceId = event.instanceUUID,
                    time = event.calledAt.toLong(),
                    callId = event.methodCallUUID,
                    methodFqName = event.methodSignature,
                )
            }

            is BackInTimeDebugServiceEvent.NotifyValueChange -> {
                InstanceEventData.StateChange(
                    instanceId = event.instanceUUID,
                    time = 0, // FIXME
                    callId = event.methodCallUUID,
                    propertyFqName = event.propertySignature,
                    jsonValue = event.value,
                )
            }

            is BackInTimeDebugServiceEvent.RegisterInstance -> {
                InstanceEventData.Register(
                    instanceId = event.instanceUUID,
                    time = event.registeredAt.toLong(),
                    classInfo = ClassInfo(
                        classSignature = event.classSignature,
                        superClassSignature = event.superClassSignature,
                        properties = event.properties.map(PropertyInfo::fromString),
                    ),
                )
            }

            is BackInTimeDebugServiceEvent.RegisterRelationship,
            is BackInTimeDebugServiceEvent.Ping,
            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult,
            is BackInTimeDebugServiceEvent.Error,
            is BackInTimeDebuggerEvent.CheckInstanceAlive,
            is BackInTimeDebuggerEvent.Error,
            is BackInTimeDebuggerEvent.ForceSetPropertyValue,
            is BackInTimeDebuggerEvent.Ping,
                -> null
        }
    }
}