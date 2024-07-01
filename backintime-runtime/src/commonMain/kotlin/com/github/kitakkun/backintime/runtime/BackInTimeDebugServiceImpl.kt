package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.connector.BackInTimeConnector
import com.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException
import kotlin.coroutines.CoroutineContext

/**
 * Singleton service for back-in-time debugger
 */
@Suppress("unused")
object BackInTimeDebugServiceImpl : CoroutineScope, BackInTimeDebugService {
    private val instanceManager = BackInTimeInstanceManagerImpl()
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + SupervisorJob()
    private val serviceEventDispatchQueue = mutableListOf<BackInTimeDebugServiceEvent>()

    private var connector: BackInTimeConnector? = null
    private var processDebuggerEventJob: Job? = null
    private var observeConnectedFlowJob: Job? = null

    init {
        // clean up instances that are garbage collected
        launch {
            while (true) {
                instanceManager.cleanGarbageCollectedReferences()
                delay(1000 * 15)
            }
        }
    }

    fun setConnector(connector: BackInTimeConnector) {
        this.connector = connector
    }

    override fun startService() {
        val connector = this.connector ?: return
        connector.connect()
        processDebuggerEventJob = launch {
            connector.receiveEventAsFlow().collect(::processDebuggerEvent)
        }
        observeConnectedFlowJob = launch {
            connector.connectedFlow.filter { it }.collect {
                serviceEventDispatchQueue.forEach { event ->
                    connector.sendEvent(event)
                }
                serviceEventDispatchQueue.clear()
            }
        }
    }

    override fun stopService() {
        processDebuggerEventJob?.cancel()
        observeConnectedFlowJob?.cancel()
        connector?.disconnect()
        processDebuggerEventJob = null
        observeConnectedFlowJob = null
        connector = null
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
        if (resultEventForDebugger != null) {
            sendOrQueue(resultEventForDebugger)
        }
    }

    /**
     * process event from [com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer]
     * consume it and generate [BackInTimeDebugServiceEvent]
     */
    override fun processDebuggerEvent(event: BackInTimeDebuggerEvent) {
        val resultEventForDebugger = when (event) {
            is BackInTimeDebuggerEvent.CheckInstanceAlive -> {
                val result = event.instanceUUIDs.map { uuid -> instanceManager.getInstanceById(uuid) != null }
                BackInTimeDebugServiceEvent.CheckInstanceAliveResult(event.instanceUUIDs, result)
            }

            is BackInTimeDebuggerEvent.ForceSetPropertyValue -> {
                forceSetValue(event.instanceUUID, event.propertyFqName, event.value)
                null
            }

            is BackInTimeDebuggerEvent.Ping -> {
                // do nothing
                null
            }
        }
        if (resultEventForDebugger != null) {
            sendOrQueue(resultEventForDebugger)
        }
    }

    private fun sendOrQueue(event: BackInTimeDebugServiceEvent) {
        if (connector?.connected == true) {
            connector?.sendEvent(event)
        } else {
            serviceEventDispatchQueue.add(event)
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

    private fun registerRelationship(event: BackInTimeDebuggableInstanceEvent.RegisterRelationShip): BackInTimeDebugServiceEvent {
        return BackInTimeDebugServiceEvent.RegisterRelationship(
            parentUUID = event.parentInstance.backInTimeInstanceUUID,
            childUUID = event.childInstance.backInTimeInstanceUUID,
        )
    }

    private fun notifyMethodCall(event: BackInTimeDebuggableInstanceEvent.MethodCall): BackInTimeDebugServiceEvent {
        return BackInTimeDebugServiceEvent.NotifyMethodCall(
            instanceUUID = event.instance.backInTimeInstanceUUID,
            methodName = event.methodName,
            methodCallUUID = event.methodCallId,
            calledAt = Clock.System.now().epochSeconds,
        )
    }

    private fun notifyPropertyChanged(event: BackInTimeDebuggableInstanceEvent.PropertyValueChange): BackInTimeDebugServiceEvent? {
        return try {
            val serializedValue = event.instance.serializeValue(event.propertyFqName, event.propertyValue)
            BackInTimeDebugServiceEvent.NotifyValueChange(
                instanceUUID = event.instance.backInTimeInstanceUUID,
                propertyFqName = event.propertyFqName,
                value = serializedValue,
                methodCallUUID = event.methodCallId,
            )
        } catch (e: SerializationException) {
            BackInTimeDebugServiceEvent.Error(e.message ?: "Unknown error")
        }
    }

    private fun forceSetValue(instanceId: String, name: String, value: String) {
        val targetInstance = instanceManager.getInstanceById(instanceId) ?: return
        try {
            val deserializedValue = targetInstance.deserializeValue(name, value)
            targetInstance.forceSetValue(name, deserializedValue)
        } catch (e: SerializationException) {
            sendOrQueue(BackInTimeDebugServiceEvent.Error(e.message ?: "Unknown error"))
        }
    }
}
