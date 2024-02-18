package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.event.BackInTimeDebugServiceEvent
import com.github.kitakkun.backintime.runtime.event.DebuggableStateHolderEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val mutableServiceEventFlow = MutableSharedFlow<BackInTimeDebugServiceEvent>()
    val serviceEventFlow = mutableServiceEventFlow.asSharedFlow()

    private val mutableInternalErrorFlow = MutableSharedFlow<Throwable>()
    val internalErrorFlow = mutableInternalErrorFlow.asSharedFlow()

    private val internalServiceEventQueue = mutableListOf<BackInTimeDebugServiceEvent>()
    private var isConnected: Boolean = false

    /**
     * start processing events (also process queued events)
     * should be called from FlipperPlugin
     */
    fun start() {
        isConnected = true
        internalServiceEventQueue.forEach { event -> launch { mutableServiceEventFlow.emit(event) } }
        internalServiceEventQueue.clear()
    }

    /**
     * suspend processing events
     * should be called from FlipperPlugin
     */
    fun suspend() {
        isConnected = false
    }

    /**
     * send event to Flipper
     * should be called from BackInTimeDebuggable instance
     * @param event event to be sent
     */
    fun emitEvent(event: DebuggableStateHolderEvent) {
        val result = when (event) {
            is DebuggableStateHolderEvent.RegisterInstance -> register(event)
            is DebuggableStateHolderEvent.RegisterRelationShip -> registerRelationship(event)
            is DebuggableStateHolderEvent.MethodCall -> notifyMethodCall(event)
            is DebuggableStateHolderEvent.PropertyValueChange -> notifyPropertyChanged(event)
        }

        if (result != null) {
            if (isConnected) {
                launch { mutableServiceEventFlow.emit(result) }
            } else {
                internalServiceEventQueue.add(result)
            }
        }
    }

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     */
    private fun register(event: DebuggableStateHolderEvent.RegisterInstance): BackInTimeDebugServiceEvent {
        // When the instance of subclass is registered, it overrides the instance of superclass.
        instances[event.instance.backInTimeInstanceUUID] = WeakReference(event.instance)
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
            launch {
                mutableInternalErrorFlow.emit(e)
            }
            null
        }
    }


    /**
     * check if the instance is still alive
     * this function is necessary because WeakHashMap doesn't have callback when the instance is garbage collected
     * @param instanceId UUID of the instance
     */
    fun checkIfInstanceIsAlive(instanceId: String): Boolean {
        instances[instanceId]?.get() ?: run {
            instances.remove(instanceId)
            return false
        }
        return true
    }

    fun forceSetValue(instanceId: String, name: String, value: String) {
        val targetInstance = instances[instanceId]?.get() ?: run {
            instances.remove(instanceId)
            return
        }
        try {
            val deserializedValue = targetInstance.deserializeValue(name, value)
            targetInstance.forceSetValue(name, deserializedValue)
        } catch (e: SerializationException) {
            launch {
                mutableInternalErrorFlow.emit(e)
            }
        }
    }
}
