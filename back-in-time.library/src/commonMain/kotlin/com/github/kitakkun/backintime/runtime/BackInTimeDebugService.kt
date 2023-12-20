package com.github.kitakkun.backintime.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import org.jetbrains.annotations.VisibleForTesting
import java.util.WeakHashMap
import kotlin.coroutines.CoroutineContext

typealias UUIDString = String

/**
 * Singleton service for back-in-time debugger
 */
@Suppress("unused")
object BackInTimeDebugService : CoroutineScope {
    override val coroutineContext: CoroutineContext get() = Dispatchers.Default + SupervisorJob()

    @VisibleForTesting
    val instances = WeakHashMap<DebuggableStateHolderManipulator, UUIDString>()

    // because FlipperPlugin instance is created after the app is launched.
    // sometimes it fails to observe the first event.
    // temporary fix: replay the last 10 events
    private val mutableRegisteredInstanceFlow = MutableSharedFlow<InstanceInfo>(replay = 10)
    val registeredInstanceFlow = mutableRegisteredInstanceFlow.asSharedFlow()

    private val mutableNotifyValueChangeFlow = MutableSharedFlow<ValueChangeInfo>()
    val notifyValueChangeFlow = mutableNotifyValueChangeFlow.asSharedFlow()

    private val mutableNotifyMethodCallFlow = MutableSharedFlow<MethodCallInfo>()
    val notifyMethodCallFlow = mutableNotifyMethodCallFlow.asSharedFlow()

    private val mutableInternalErrorFlow = MutableSharedFlow<Throwable>()
    val internalErrorFlow = mutableInternalErrorFlow.asSharedFlow()

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     * @param instance instance to be registered. must be annotated with [DebuggableStateHolder]
     * @Param info information about the instance
     */
    fun register(instance: DebuggableStateHolderManipulator, info: InstanceInfo) {
        instances[instance] = info.uuid
        launch {
            mutableRegisteredInstanceFlow.emit(info)
        }
    }

    /**
     * check if the instance is still alive
     * this function is necessary because WeakHashMap doesn't have callback when the instance is garbage collected
     * @param instanceUUID UUID of the instance
     */
    fun checkIfInstanceIsAlive(instanceUUID: UUIDString): Boolean {
        return instances.containsValue(instanceUUID)
    }

    fun manipulate(instanceUUID: String, propertyName: String, value: String) {
        val targetInstance = instances.filterValues { it == instanceUUID }.keys.firstOrNull() ?: return
        try {
            val deserializedValue = targetInstance.deserializeValue(propertyName, value)
            targetInstance.forceSetValue(propertyName, deserializedValue)
        } catch (e: SerializationException) {
            launch {
                mutableInternalErrorFlow.emit(e)
            }
        }
    }

    fun notifyPropertyChanged(
        instance: DebuggableStateHolderManipulator,
        propertyName: String,
        value: Any?,
        methodCallUUID: String,
    ) {
        val instanceUUID = instances[instance] ?: return
        try {
            val serializedValue = instance.serializeValue(propertyName, value)
            launch {
                mutableNotifyValueChangeFlow.emit(
                    ValueChangeInfo(
                        instanceUUID = instanceUUID,
                        propertyName = propertyName,
                        value = serializedValue,
                        methodCallUUID = methodCallUUID,
                    )
                )
            }
        } catch (e: SerializationException) {
            launch {
                mutableInternalErrorFlow.emit(e)
            }
        }
    }

    fun notifyMethodCall(
        instance: DebuggableStateHolderManipulator,
        methodName: String,
        methodCallUUID: String,
    ) {
        val instanceUUID = instances[instance] ?: return
        launch {
            mutableNotifyMethodCallFlow.emit(
                MethodCallInfo(
                    instanceUUID = instanceUUID,
                    methodName = methodName,
                    methodCallUUID = methodCallUUID,
                    calledAt = System.currentTimeMillis(),
                )
            )
        }
    }
}
