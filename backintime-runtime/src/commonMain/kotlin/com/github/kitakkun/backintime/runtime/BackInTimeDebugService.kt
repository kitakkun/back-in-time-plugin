package com.github.kitakkun.backintime.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.util.PriorityQueue
import java.util.WeakHashMap
import kotlin.coroutines.CoroutineContext

/**
 * Singleton service for back-in-time debugger
 */
@Suppress("unused")
object BackInTimeDebugService : CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + SupervisorJob()

    private val instances = WeakHashMap<BackInTimeDebuggable, String>()

    private val mutableRegisteredInstanceFlow = MutableSharedFlow<InstanceInfo>()
    val registeredInstanceFlow = mutableRegisteredInstanceFlow.asSharedFlow()

    private val mutableRegisterRelationshipFlow = MutableSharedFlow<RelationshipInfo>()
    val registerRelationshipFlow = mutableRegisterRelationshipFlow.asSharedFlow()

    private val mutableNotifyValueChangeFlow = MutableSharedFlow<ValueChangeInfo>()
    val notifyValueChangeFlow = mutableNotifyValueChangeFlow.asSharedFlow()

    private val mutableNotifyMethodCallFlow = MutableSharedFlow<MethodCallInfo>()
    val notifyMethodCallFlow = mutableNotifyMethodCallFlow.asSharedFlow()

    private val mutableInternalErrorFlow = MutableSharedFlow<Throwable>()
    val internalErrorFlow = mutableInternalErrorFlow.asSharedFlow()

    private val internalEventQueue = PriorityQueue<DebuggableStateHolderEvent> { event1, event2 -> event1.priority - event2.priority }
    private var isConnected: Boolean = false

    /**
     * start processing events (also process queued events)
     * should be called from FlipperPlugin
     */
    fun start() {
        isConnected = true
        internalEventQueue.forEach(::processEvent)
        internalEventQueue.clear()
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
        if (!isConnected) {
            internalEventQueue.add(event)
            return
        }

        processEvent(event)
    }

    private fun processEvent(event: DebuggableStateHolderEvent) {
        when (event) {
            is DebuggableStateHolderEvent.RegisterInstance -> register(event)
            is DebuggableStateHolderEvent.RegisterRelationShip -> registerRelationship(event)
            is DebuggableStateHolderEvent.MethodCall -> notifyMethodCall(event)
            is DebuggableStateHolderEvent.PropertyValueChange -> notifyPropertyChanged(event)
        }
    }

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     */
    private fun register(event: DebuggableStateHolderEvent.RegisterInstance) {
        // When the instance of subclass is registered, it overrides the instance of superclass.
        instances[event.instance] = event.instance.backInTimeInstanceUUID
        launch {
            mutableRegisteredInstanceFlow.emit(event.info)
        }
    }

    private fun registerRelationship(event: DebuggableStateHolderEvent.RegisterRelationShip) {
        launch {
            mutableRegisterRelationshipFlow.emit(
                RelationshipInfo(
                    from = event.parentInstance.backInTimeInstanceUUID,
                    to = event.childInstance.backInTimeInstanceUUID,
                )
            )
        }
    }

    private fun notifyMethodCall(event: DebuggableStateHolderEvent.MethodCall) {
        val instanceId = instances[event.instance] ?: return
        launch {
            mutableNotifyMethodCallFlow.emit(
                MethodCallInfo(
                    instanceUUID = instanceId,
                    methodName = event.methodName,
                    methodCallUUID = event.methodCallId,
                    calledAt = System.currentTimeMillis(),
                )
            )
        }
    }

    private fun notifyPropertyChanged(event: DebuggableStateHolderEvent.PropertyValueChange) {
        val instanceUUID = instances[event.instance] ?: return
        try {
            val serializedValue = event.instance.serializeValue(event.propertyName, event.propertyValue)
            launch {
                mutableNotifyValueChangeFlow.emit(
                    ValueChangeInfo(
                        instanceUUID = instanceUUID,
                        propertyName = event.propertyName,
                        value = serializedValue,
                        methodCallUUID = event.methodCallId,
                    )
                )
            }
        } catch (e: SerializationException) {
            launch {
                mutableInternalErrorFlow.emit(e)
            }
        }
    }


    /**
     * check if the instance is still alive
     * this function is necessary because WeakHashMap doesn't have callback when the instance is garbage collected
     * @param instanceId UUID of the instance
     */
    fun checkIfInstanceIsAlive(instanceId: String): Boolean {
        return instances.containsValue(instanceId)
    }

    fun forceSetValue(instanceId: String, name: String, value: String) {
        val targetInstance = instances.filterValues { it == instanceId }.keys.firstOrNull() ?: return
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
