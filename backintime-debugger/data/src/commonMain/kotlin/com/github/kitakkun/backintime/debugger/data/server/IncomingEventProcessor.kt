package com.github.kitakkun.backintime.debugger.data.server

import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepository
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepository
import com.github.kitakkun.backintime.debugger.data.repository.MethodCallInfoRepository
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent

interface IncomingEventProcessor {
    suspend fun processEvent(sessionId: String, event: BackInTimeDebugServiceEvent): BackInTimeDebuggerEvent?
}

class IncomingEventProcessorImpl(
    private val classInfoRepository: ClassInfoRepository,
    private val instanceRepository: InstanceRepository,
    private val logRepository: EventLogRepository,
    private val methodCallInfoRepository: MethodCallInfoRepository,
) : IncomingEventProcessor {
    override suspend fun processEvent(sessionId: String, event: BackInTimeDebugServiceEvent): BackInTimeDebuggerEvent? {
        logRepository.insert(sessionId, event)

        return when (event) {
            is BackInTimeDebugServiceEvent.RegisterInstance -> register(sessionId, event)
            is BackInTimeDebugServiceEvent.NotifyMethodCall -> notifyMethodCall(sessionId, event)
            is BackInTimeDebugServiceEvent.NotifyValueChange -> notifyValueChange(sessionId, event)
            is BackInTimeDebugServiceEvent.RegisterRelationship -> registerRelationship(sessionId, event)
            is BackInTimeDebugServiceEvent.CheckInstanceAliveResult -> checkInstanceAliveResult(sessionId, event)
            is BackInTimeDebugServiceEvent.Error -> TODO("Not yet implemented")
            is BackInTimeDebugServiceEvent.Ping -> null // ignore
        }
    }

    private suspend fun register(sessionId: String, event: BackInTimeDebugServiceEvent.RegisterInstance): BackInTimeDebuggerEvent? {
        instanceRepository.insert(
            id = event.instanceUUID,
            className = event.className,
            registeredAt = event.registeredAt,
            sessionId = sessionId,
        )
        classInfoRepository.insert(
            className = event.className,
            superClassName = event.superClassName,
            properties = event.properties,
            sessionId = sessionId,
        )
        return null
    }

    private suspend fun notifyMethodCall(sessionId: String, event: BackInTimeDebugServiceEvent.NotifyMethodCall): BackInTimeDebuggerEvent? {
        methodCallInfoRepository.insert(
            sessionId = sessionId,
            instanceUUID = event.instanceUUID,
            className = "FIXME", // FIXME: get class name
            methodName = event.methodName,
            callId = event.methodCallUUID,
        )
        return null
    }

    private suspend fun notifyValueChange(sessionId: String, event: BackInTimeDebugServiceEvent.NotifyValueChange): BackInTimeDebuggerEvent? {
        methodCallInfoRepository.insertValueChange(
            sessionId = sessionId,
            instanceUUID = event.instanceUUID,
            className = "FIXME", // FIXME: get class name
            callId = event.methodCallUUID,
            propertyName = event.propertyName,
            propertyValue = event.value,
        )
        return null
    }

    private suspend fun registerRelationship(sessionId: String, event: BackInTimeDebugServiceEvent.RegisterRelationship): BackInTimeDebuggerEvent? {
        instanceRepository.addChildInstance(
            sessionId = sessionId,
            parentId = event.parentUUID,
            childId = event.childUUID,
        )
        return null
    }

    private suspend fun checkInstanceAliveResult(sessionId: String, event: BackInTimeDebugServiceEvent.CheckInstanceAliveResult): BackInTimeDebuggerEvent? {
        event.instanceUUIDs.zip(event.result).forEach {
            instanceRepository.updateAlive(sessionId, it.first, it.second)
        }
        return null
    }
}
