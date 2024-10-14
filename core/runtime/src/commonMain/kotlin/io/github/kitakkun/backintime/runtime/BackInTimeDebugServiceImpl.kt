package io.github.kitakkun.backintime.runtime

import io.github.kitakkun.backintime.runtime.connector.BackInTimeWebSocketConnector
import io.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent
import io.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import io.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException

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
     * process event from [io.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer]
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
                    instanceId = event.instanceUUID,
                    propertyOwnerClassName = event.ownerClassFqName,
                    propertyName = event.propertyName,
                    value = event.value,
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
            className = event.className,
            superClassName = event.superClassName,
            properties = event.properties,
            registeredAt = Clock.System.now().epochSeconds,
        )
    }

    private fun registerRelationship(event: BackInTimeDebuggableInstanceEvent.RegisterRelationShip): BackInTimeDebugServiceEvent = BackInTimeDebugServiceEvent.RegisterRelationship(
        parentUUID = event.parentInstance.backInTimeInstanceUUID,
        childUUID = event.childInstance.backInTimeInstanceUUID,
    )

    private fun notifyMethodCall(event: BackInTimeDebuggableInstanceEvent.MethodCall): BackInTimeDebugServiceEvent = BackInTimeDebugServiceEvent.NotifyMethodCall(
        instanceUUID = event.instance.backInTimeInstanceUUID,
        methodName = event.methodName,
        methodCallUUID = event.methodCallId,
        calledAt = Clock.System.now().epochSeconds,
        ownerClassFqName = event.ownerClassFqName,
    )

    private fun notifyPropertyChanged(event: BackInTimeDebuggableInstanceEvent.PropertyValueChange): BackInTimeDebugServiceEvent {
        try {
            val serializedValue = event.instance.serializeValue(
                propertyOwnerClassFqName = event.ownerClassFqName,
                propertyName = event.propertyName,
                value = event.propertyValue,
            )
            return BackInTimeDebugServiceEvent.NotifyValueChange(
                instanceUUID = event.instance.backInTimeInstanceUUID,
                propertyName = event.propertyName,
                ownerClassFqName = event.ownerClassFqName,
                value = serializedValue,
                methodCallUUID = event.methodCallId,
            )
        } catch (e: SerializationException) {
            return BackInTimeDebugServiceEvent.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * force update the state of a property. This method can be used for back-in-time debugging.
     * @param instanceId the identifier for the property owner class
     * @param propertyOwnerClassName the full-qualified class name of the property owner class.
     * @param propertyName the name of the target property.
     * @param value Json-encoded value which will be assigned to the property.
     */
    private fun forceSetValue(
        instanceId: String,
        propertyOwnerClassName: String,
        propertyName: String,
        value: String,
    ) {
        val targetInstance = instanceManager.getInstanceById(instanceId) ?: return
        try {
            val deserializedValue = targetInstance.deserializeValue(
                propertyOwnerClassFqName = propertyOwnerClassName,
                propertyName = propertyName,
                value = value,
            )
            targetInstance.forceSetValue(
                propertyOwnerClassFqName = propertyOwnerClassName,
                propertyName = propertyName,
                value = deserializedValue,
            )
        } catch (e: SerializationException) {
            sendOrQueueEvent(BackInTimeDebugServiceEvent.Error(e.message ?: "Unknown error"))
        }
    }
}
