package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.converter.BackInTimeJSONConverter
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
    val instances = WeakHashMap<Any, UUIDString>()

    private val mutableValueChangeFlow = MutableSharedFlow<ValueChangeData>()
    val valueChangeFlow = mutableValueChangeFlow.asSharedFlow()

    // FIXME: this should be injected from outside
    var jsonConverter: BackInTimeJSONConverter? = BackInTimeDefaultJSONConverter()

    /**
     * register instance for debugging
     * if the instance is garbage collected, it will be automatically removed from the list.
     * @param instance instance to be registered. must be annotated with [DebuggableStateHolder]
     */
    fun register(instance: Any): UUIDString {
        val uuidString = UUID.randomUUID().toString()
        instances[instance] = uuidString
        return uuidString
    }

    fun manipulate(
        instanceUUID: UUIDString,
        propertyName: String,
        value: String,
        valueTypeQualifiedName: String
    ) {
        val valueType = Class.forName(valueTypeQualifiedName).kotlin
        val deserializedValue = jsonConverter?.deserialize(
            value = value,
            valueType = valueType,
        )
        instances
            .filterValues { uuidString -> uuidString == instanceUUID }
            .keys
            .filterIsInstance<DebuggableStateHolderManipulator>()
            .firstOrNull()?.forceSetPropertyValueForBackInTimeDebug(propertyName, deserializedValue)
    }

    @Suppress("unused")
    fun notifyPropertyChanged(
        instance: Any,
        propertyName: String,
        value: Any?,
        valueTypeQualifiedName: String,
    ) {
        val serializedValue = jsonConverter?.serialize(value)
        launch {
            mutableValueChangeFlow.emit(
                ValueChangeData(
                    instanceUUID = instances[instance] ?: return@launch,
                    propertyName = propertyName,
                    value = serializedValue.toString(),
                    valueType = valueTypeQualifiedName,
                )
            )
        }
    }
}
