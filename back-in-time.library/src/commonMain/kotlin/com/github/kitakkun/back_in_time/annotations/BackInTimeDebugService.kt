package com.github.kitakkun.back_in_time.annotations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

typealias IdentityHashCode = Int
typealias DebugTargetWeakReference = WeakReference<Any>

/**
 * Singleton service for back-in-time debugger
 */
object BackInTimeDebugService : CoroutineScope {
    override val coroutineContext: kotlin.coroutines.CoroutineContext get() = Dispatchers.Default + SupervisorJob()

    val instances = mutableMapOf<IdentityHashCode, DebugTargetWeakReference>()
    private val mutableValueChangeFlow = MutableSharedFlow<ValueChangeData>()
    val valueChangeFlow = mutableValueChangeFlow.asSharedFlow()

    /**
     * register instance for debugging
     * @param instance instance to be registered. must be annotated with [DebuggableStateHolder]
     */
    fun register(instance: Any) {
        val hashCode = System.identityHashCode(instance)
        if (instances.containsKey(hashCode)) throw IllegalStateException("already registered: $instance")
        instances[hashCode] = WeakReference(instance)
    }

    /**
     * unregister instance for debugging
     * @param instance instance to be unregistered. must be annotated with [DebuggableStateHolder]
     */
    fun unregister(instance: Any) {
        if (!instances.containsKey(System.identityHashCode(instance))) throw IllegalStateException("not registered: $instance")
        instances.remove(System.identityHashCode(instance))
    }

    fun manipulate(instanceKey: IdentityHashCode, paramKey: String, value: String) {
        (instances[instanceKey]?.get() as? DebuggableStateHolderManipulator)?.forceSetParameterForBackInTimeDebug(
            paramKey,
            value
        )
    }

    fun notifyPropertyChanged(instance: Any, propertyName: String, value: Any?) {
        launch {
            mutableValueChangeFlow.emit(
                ValueChangeData(
                    System.identityHashCode(instance),
                    propertyName,
                    value.toString()
                )
            )
        }
    }
}

