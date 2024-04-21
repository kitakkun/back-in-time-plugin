package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.connector.BackInTimeConnector
import com.github.kitakkun.backintime.runtime.event.DebuggableStateHolderEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.websocket.event.BackInTimeDebuggerEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.SerializationException
import kotlin.coroutines.CoroutineContext

/**
 * Singleton service for back-in-time debugger
 */
@Suppress("unused")
object BackInTimeDebugService : CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + SupervisorJob()

    private val instances = mutableMapOf<String, WeakReference<BackInTimeDebuggable>>()
    private val serviceEventDispatchQueue = mutableListOf<BackInTimeDebugServiceEvent>()

    private var connector: BackInTimeConnector? = null
    private var processDebuggerEventJob: Job? = null
    private var observeConnectedFlowJob: Job? = null

    init {
        // clean up instances that are garbage collected
        startInstanceCleanUpJob()
    }

    fun setConnector(connector: BackInTimeConnector) {
        this.connector = connector
    }

    fun startService() {
        val connector = this.connector ?: return
        connector.connect()
        observeConnectedFlowJob = launch {
            connector.connectedFlow.filter { it }.first()
            processDebuggerEventJob = launch {
                connector.receiveEventAsFlow().collect collectReceive@{ event ->
                    val result = processDebuggerRequest(event) ?: return@collectReceive
                    sendOrQueue(result)
                }
            }
            serviceEventDispatchQueue.forEach { event ->
                connector.sendEvent(event)
            }
            serviceEventDispatchQueue.clear()
        }
    }

    fun stopService() {
        processDebuggerEventJob?.cancel()
        observeConnectedFlowJob?.cancel()
        connector?.disconnect()
        processDebuggerEventJob = null
        observeConnectedFlowJob = null
        connector = null
    }

    private fun startInstanceCleanUpJob() {
        launch {
            while (true) {
                instances.keys.forEach { key ->
                    val instance = instances[key] ?: return@forEach
                    if (instance.get() == null) {
                        instances.remove(key)
                    }
                }
                delay(1000 * 15)
            }
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
     * process event from [com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer]
     * consume it and generate [BackInTimeDebugServiceEvent]
     */
    private fun processDebuggerRequest(event: BackInTimeDebuggerEvent): BackInTimeDebugServiceEvent? {
        return when (event) {
            is BackInTimeDebuggerEvent.CheckInstanceAlive -> {
                val result = event.instanceUUIDs.map { uuid -> instances[uuid]?.get() != null }
                BackInTimeDebugServiceEvent.CheckInstanceAliveResult(event.instanceUUIDs, result)
            }

            is BackInTimeDebuggerEvent.ForceSetPropertyValue -> {
                forceSetValue(event.instanceUUID, event.propertyName, event.value)
                null
            }

            is BackInTimeDebuggerEvent.Ping -> null // do nothing

            is BackInTimeDebuggerEvent.Error -> null // do nothing
        }
    }

    /**
     * process event from [BackInTimeDebuggable] instance
     * consume it and generate [BackInTimeDebugServiceEvent]
     */
    private fun processStateHolderEvent(event: DebuggableStateHolderEvent): BackInTimeDebugServiceEvent? {
        return when (event) {
            is DebuggableStateHolderEvent.RegisterInstance -> register(event)
            is DebuggableStateHolderEvent.RegisterRelationShip -> registerRelationship(event)
            is DebuggableStateHolderEvent.MethodCall -> notifyMethodCall(event)
            is DebuggableStateHolderEvent.PropertyValueChange -> notifyPropertyChanged(event)
        }
    }

    /**
     * send event to debugger from [BackInTimeDebuggable] instance
     * should be called from compiler-generate code inside [BackInTimeDebuggable] classes
     */
    fun emitEvent(event: DebuggableStateHolderEvent) {
        val result = processStateHolderEvent(event) ?: return
        sendOrQueue(result)
    }

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     */
    private fun register(event: DebuggableStateHolderEvent.RegisterInstance): BackInTimeDebugServiceEvent {
        // When the instance of subclass is registered, it overrides the instance of superclass.
        instances[event.instance.backInTimeInstanceUUID] = weakReferenceOf(event.instance)
        return BackInTimeDebugServiceEvent.RegisterInstance(
            instanceUUID = event.instance.backInTimeInstanceUUID,
            className = event.className,
            superClassName = event.superClassName,
            properties = event.properties,
            registeredAt = Clock.System.now().epochSeconds,
        )
    }

    private fun registerRelationship(event: DebuggableStateHolderEvent.RegisterRelationShip): BackInTimeDebugServiceEvent {
        return BackInTimeDebugServiceEvent.RegisterRelationship(
            parentUUID = event.parentInstance.backInTimeInstanceUUID,
            childUUID = event.childInstance.backInTimeInstanceUUID,
        )
    }

    private fun notifyMethodCall(event: DebuggableStateHolderEvent.MethodCall): BackInTimeDebugServiceEvent? {
        return BackInTimeDebugServiceEvent.NotifyMethodCall(
            instanceUUID = event.instance.backInTimeInstanceUUID,
            methodName = event.methodName,
            methodCallUUID = event.methodCallId,
            calledAt = Clock.System.now().epochSeconds,
        )
    }

    private fun notifyPropertyChanged(event: DebuggableStateHolderEvent.PropertyValueChange): BackInTimeDebugServiceEvent? {
        return try {
            val serializedValue = event.instance.serializeValue(event.propertyName, event.propertyValue)
            BackInTimeDebugServiceEvent.NotifyValueChange(
                instanceUUID = event.instance.backInTimeInstanceUUID,
                propertyName = event.propertyName,
                value = serializedValue,
                methodCallUUID = event.methodCallId,
            )
        } catch (e: SerializationException) {
            sendOrQueue(BackInTimeDebugServiceEvent.Error(e.message ?: "Unknown error"))
            null
        }
    }

    private fun forceSetValue(instanceId: String, name: String, value: String) {
        val targetInstance = instances.filterKeys { it == instanceId }.values.firstOrNull()?.get() ?: return
        try {
            val deserializedValue = targetInstance.deserializeValue(name, value)
            targetInstance.forceSetValue(name, deserializedValue)
        } catch (e: SerializationException) {
            sendOrQueue(BackInTimeDebugServiceEvent.Error(e.message ?: "Unknown error"))
        }
    }
}
