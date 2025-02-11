package com.kitakkun.backintime.tooling.idea.service

import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeWebSocketEvent
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkun.backintime.tooling.model.PropertyInfo

class BackInTimeEventConverter {
    fun convertToEntity(
        sessionId: String,
        event: BackInTimeWebSocketEvent,
    ): EventEntity? {
        return when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> {
                EventEntity.Instance.Register(
                    sessionId = sessionId,
                    instanceId = event.instanceUUID,
                    time = event.time.toLong(),
                    classInfo = ClassInfo(
                        classSignature = event.classSignature,
                        superClassSignature = event.superClassSignature,
                        properties = event.properties.map(PropertyInfo::fromString),
                    )
                )
            }

            is BackInTimeDebugServiceEvent.NotifyMethodCall -> {
                EventEntity.Instance.MethodInvocation(
                    sessionId = sessionId,
                    instanceId = event.instanceUUID,
                    time = event.time.toLong(),
                    methodSignature = event.methodSignature,
                    callId = event.methodCallUUID,
                )
            }

            is BackInTimeDebugServiceEvent.NotifyValueChange -> {
                EventEntity.Instance.StateChange(
                    sessionId = sessionId,
                    instanceId = event.instanceUUID,
                    time = event.time.toLong(),
                    propertySignature = event.propertySignature,
                    newValueAsJson = event.value,
                    callId = event.methodCallUUID,
                )
            }

            is BackInTimeDebugServiceEvent.RegisterRelationship -> {
                EventEntity.Instance.NewDependency(
                    sessionId = sessionId,
                    instanceId = event.parentUUID,
                    time = event.time.toLong(),
                    dependencyInstanceId = event.childUUID,
                )
            }

            is BackInTimeDebuggerEvent.ForceSetPropertyValue -> {
                EventEntity.Instance.BackInTime(
                    sessionId = sessionId,
                    instanceId = event.targetInstanceId,
                    time = event.time.toLong(),
                    jsonValues = mapOf(),
                    destinationPointEventId = null,
                )
            }

            is BackInTimeDebugServiceEvent.Error -> {
                EventEntity.System.AppError(
                    sessionId = sessionId,
                    time = event.time.toLong(),
                    message = event.message,
                )
            }

            is BackInTimeDebuggerEvent.Error -> {
                EventEntity.System.DebuggerError(
                    sessionId = sessionId,
                    time = event.time.toLong(),
                    message = event.message,
                )
            }

            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> {
                EventEntity.System.CheckInstanceAliveResult(
                    sessionId = sessionId,
                    time = event.time.toLong(),
                    isAlive = event.isAlive,
                )
            }

            is BackInTimeDebuggerEvent.CheckInstanceAlive -> {
                EventEntity.System.CheckInstanceAlive(
                    sessionId = sessionId,
                    time = event.time.toLong(),
                )
            }

            is BackInTimeDebugServiceEvent.Ping -> {
                null
            }

            is BackInTimeDebuggerEvent.Ping -> {
                null
            }
        }
    }
}