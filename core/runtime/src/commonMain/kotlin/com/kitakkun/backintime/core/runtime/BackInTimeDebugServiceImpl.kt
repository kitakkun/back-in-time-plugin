package com.kitakkun.backintime.core.runtime

import com.kitakkun.backintime.core.runtime.connector.BackInTimeWebSocketConnector
import com.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebugServiceEvent
import com.kitakkun.backintime.core.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Singleton service for back-in-time debugger
 */
@Suppress("unused")
class BackInTimeDebugServiceImpl(
    dispatcher: CoroutineDispatcher,
) : BackInTimeDebugService {
    private val instanceManager: BackInTimeInstanceManager = BackInTimeInstanceManagerImpl()
    private val coroutineScope: CoroutineScope = CoroutineScope(dispatcher + SupervisorJob())
    private var connector: BackInTimeWebSocketConnector? = null
    private var sendEventQueue = mutableListOf<BackInTimeDebugServiceEvent>()

    override fun setConnector(connector: BackInTimeWebSocketConnector) {
        this.connector = connector
    }

    override fun startService() {
        coroutineScope.coroutineContext.cancelChildren()
        val connector = connector ?: return
        coroutineScope.launch {
            try {
                val receiveEventsFlow = connector.connect()

                sendEventQueue.forEach {
                    connector.sendEventToDebugger(it)
                }

                sendEventQueue.clear()

                launch {
                    receiveEventsFlow.collect(::processDebuggerEvent)
                }
                launch {
                    while (true) {
                        instanceManager.cleanGarbageCollectedReferences()
                        delay(1000 * 15)
                    }
                }

                connector.awaitCloseSession()
            } catch (e: Throwable) {
                // do nothing
            } finally {
                delay(3000)
                startService()
            }
        }
    }

    override fun stopService() {
        coroutineScope.coroutineContext.cancelChildren()
        coroutineScope.launch {
            connector?.close()
        }
    }

    /**
     * process event from [BackInTimeDebuggable] instance
     * consume it and generate [BackInTimeDebugServiceEvent]
     */
    override fun processInstanceEvent(event: BackInTimeDebuggableInstanceEvent) {
        val resultEventForDebugger = when (event) {
            is BackInTimeDebuggableInstanceEvent.RegisterTarget -> register(event)
            is BackInTimeDebuggableInstanceEvent.RegisterRelationShip -> registerRelationship(event)
            is BackInTimeDebuggableInstanceEvent.MethodCall -> notifyMethodCall(event)
            is BackInTimeDebuggableInstanceEvent.PropertyValueChange -> notifyPropertyChanged(event)
            is BackInTimeDebuggableInstanceEvent.Error -> error(event)
        }
        sendOrQueueEvent(resultEventForDebugger)
    }

    private fun sendOrQueueEvent(event: BackInTimeDebugServiceEvent) {
        connector?.let {
            coroutineScope.launch {
                it.sendEventToDebugger(event)
            }
        } ?: sendEventQueue.add(event)
    }

    /**
     * process event from [com.kitakkun.backintime.core.websocket.server.BackInTimeWebSocketServer]
     * consume it and generate [BackInTimeDebugServiceEvent]
     */
    override fun processDebuggerEvent(event: BackInTimeDebuggerEvent) {
        val resultEventForDebugger = when (event) {
            is BackInTimeDebuggerEvent.CheckInstanceAlive -> {
                val result = event.instanceUUIDs.associateWith { uuid -> instanceManager.getInstanceById(uuid) != null }
                BackInTimeDebugServiceEvent.CheckInstanceAliveResult(result)
            }

            is BackInTimeDebuggerEvent.ForceSetPropertyValue -> {
                forceSetValue(
                    instanceId = event.targetInstanceId,
                    propertySignature = event.propertySignature,
                    jsonValue = event.jsonValue,
                )
                null
            }

            is BackInTimeDebuggerEvent.Ping,
            is BackInTimeDebuggerEvent.Error,
                -> null
        }
        if (resultEventForDebugger != null) {
            sendOrQueueEvent(resultEventForDebugger)
        }
    }

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     */
    private fun register(event: BackInTimeDebuggableInstanceEvent.RegisterTarget): BackInTimeDebugServiceEvent {
        instanceManager.register(event.instance)
        return BackInTimeDebugServiceEvent.RegisterInstance(
            instanceUUID = event.instance.backInTimeInstanceUUID,
            classSignature = event.classSignature,
            superClassSignature = event.superClassSignature,
            properties = event.properties,
            registeredAt = Clock.System.now().epochSeconds.toInt(),
        )
    }

    private fun registerRelationship(event: BackInTimeDebuggableInstanceEvent.RegisterRelationShip): BackInTimeDebugServiceEvent = BackInTimeDebugServiceEvent.RegisterRelationship(
        parentUUID = event.parentInstance.backInTimeInstanceUUID,
        childUUID = event.childInstance.backInTimeInstanceUUID,
    )

    private fun notifyMethodCall(event: BackInTimeDebuggableInstanceEvent.MethodCall): BackInTimeDebugServiceEvent = BackInTimeDebugServiceEvent.NotifyMethodCall(
        instanceUUID = event.instance.backInTimeInstanceUUID,
        methodSignature = event.methodSignature,
        methodCallUUID = event.methodCallId,
        calledAt = Clock.System.now().epochSeconds.toInt(),
    )

    private fun notifyPropertyChanged(event: BackInTimeDebuggableInstanceEvent.PropertyValueChange): BackInTimeDebugServiceEvent {
        return BackInTimeDebugServiceEvent.NotifyValueChange(
            instanceUUID = event.instance.backInTimeInstanceUUID,
            value = event.propertyValue,
            propertySignature = event.propertySignature,
            methodCallUUID = event.methodCallId,
        )
    }

    /**
     * force update the state of a property. This method can be used for back-in-time debugging.
     * @param instanceId the identifier for the property owner class
     * @param propertySignature the signature of the target property.
     * @param jsonValue Json-encoded value which will be assigned to the property.
     */
    private fun forceSetValue(
        instanceId: String,
        propertySignature: String,
        jsonValue: String,
    ) {
        val targetInstance = instanceManager.getInstanceById(instanceId) ?: return
        targetInstance.forceSetValue(propertySignature = propertySignature, jsonValue = jsonValue)
    }

    private fun error(event: BackInTimeDebuggableInstanceEvent.Error): BackInTimeDebugServiceEvent {
        return BackInTimeDebugServiceEvent.Error(event.exception.message ?: "Unknown error")
    }
}
