package com.github.kitakkun.backintime.runtime

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import java.util.UUID
import java.util.WeakHashMap

typealias UUIDString = String

/**
 * Singleton service for back-in-time debugger
 */
object BackInTimeDebugService : CoroutineScope {
    override val coroutineContext: kotlin.coroutines.CoroutineContext get() = Dispatchers.Default + SupervisorJob()

    @VisibleForTesting
    val instances = WeakHashMap<DebuggableStateHolderManipulator, UUIDString>()

    private val mutableValueChangeFlow = MutableSharedFlow<ValueChangeData>()
    val valueChangeFlow = mutableValueChangeFlow.asSharedFlow()

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     * @param instance instance to be registered. must be annotated with [DebuggableStateHolder]
     */
    fun register(instance: DebuggableStateHolderManipulator): UUIDString {
        val uuidString = UUID.randomUUID().toString()
        instances[instance] = uuidString
        return uuidString
    }

    fun manipulate(
        instanceUUID: UUIDString,
        propertyName: String,
        value: Any?,
    ) {
        instances
            .filterValues { uuidString -> uuidString == instanceUUID }
            .keys
            .firstOrNull()?.forceSetPropertyValueForBackInTimeDebug(propertyName, value)
    }

    @Suppress("unused")
    fun notifyPropertyChanged(
        instance: DebuggableStateHolderManipulator,
        propertyName: String,
        value: Any?,
        valueTypeQualifiedName: String,
    ) {
        launch {
            mutableValueChangeFlow.emit(
                ValueChangeData(
                    instanceUUID = instances[instance] ?: return@launch,
                    propertyName = propertyName,
                    value = value,
                    valueType = valueTypeQualifiedName,
                )
            )
        }
    }
}
