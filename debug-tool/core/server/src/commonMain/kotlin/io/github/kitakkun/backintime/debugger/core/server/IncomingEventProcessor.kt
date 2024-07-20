package io.github.kitakkun.backintime.debugger.core.server

import io.github.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.debugger.core.data.ClassInfoRepository
import io.github.kitakkun.backintime.debugger.core.data.EventLogRepository
import io.github.kitakkun.backintime.debugger.core.data.InstanceRepository
import io.github.kitakkun.backintime.debugger.core.data.MethodCallRepository
import io.github.kitakkun.backintime.debugger.core.data.ValueChangeRepository

interface IncomingEventProcessor {
    suspend fun processEvent(sessionId: String, event: BackInTimeDebugServiceEvent)
}

class IncomingEventProcessorImpl(
    private val classInfoRepository: ClassInfoRepository,
    private val instanceRepository: InstanceRepository,
    private val logRepository: EventLogRepository,
    private val methodCallInfoRepository: MethodCallRepository,
    private val valueChangeRepository: ValueChangeRepository,
) : IncomingEventProcessor {
    override suspend fun processEvent(sessionId: String, event: BackInTimeDebugServiceEvent) {
        logRepository.insert(sessionId, event)

        when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> register(sessionId, event)
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> notifyMethodCall(sessionId, event)
            is BackInTimeDebugServiceEvent.NotifyValueChange -> notifyValueChange(sessionId, event)
            is BackInTimeDebugServiceEvent.RegisterRelationship -> registerRelationship(sessionId, event)
            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> checkInstanceAliveResult(sessionId, event)
            is BackInTimeDebugServiceEvent.Error -> TODO("Not yet implemented")
            is BackInTimeDebugServiceEvent.Ping -> Unit // ignore
        }
    }

    private suspend fun register(sessionId: String, event: BackInTimeDebugServiceEvent.RegisterInstance) {
        val existingInstance = instanceRepository.select(sessionId, event.instanceUUID)
        if (existingInstance != null) {
            instanceRepository.updateClassName(sessionId, event.instanceUUID, event.className)
        } else {
            instanceRepository.insert(
                id = event.instanceUUID,
                className = event.className,
                registeredAt = event.registeredAt,
                sessionId = sessionId,
            )
        }
        classInfoRepository.insert(
            className = event.className,
            superClassName = event.superClassName,
            properties = event.properties,
            sessionId = sessionId,
        )
    }

    private suspend fun notifyMethodCall(sessionId: String, event: BackInTimeDebugServiceEvent.NotifyMethodCall) {
        methodCallInfoRepository.insert(
            sessionId = sessionId,
            instanceUUID = event.instanceUUID,
            className = event.ownerClassFqName,
            methodName = event.methodName,
            callId = event.methodCallUUID,
            calledAt = event.calledAt,
            methodCallId = event.methodCallUUID,
        )
    }

    private suspend fun notifyValueChange(sessionId: String, event: BackInTimeDebugServiceEvent.NotifyValueChange) {
        valueChangeRepository.insert(
            sessionId = sessionId,
            instanceId = event.instanceUUID,
            ownerClassName = event.ownerClassFqName,
            methodCallId = event.methodCallUUID,
            propertyName = event.propertyName,
            value = event.value,
        )
    }

    private suspend fun registerRelationship(sessionId: String, event: BackInTimeDebugServiceEvent.RegisterRelationship) {
        instanceRepository.addChildInstance(
            sessionId = sessionId,
            parentId = event.parentUUID,
            childId = event.childUUID,
        )
    }

    private suspend fun checkInstanceAliveResult(sessionId: String, event: BackInTimeDebugServiceEvent.CheckInstanceAliveResult) {
        event.isAlive.forEach { (instanceUUID, isAlive) ->
            instanceRepository.updateAlive(sessionId, instanceUUID, isAlive)
        }
    }
}
