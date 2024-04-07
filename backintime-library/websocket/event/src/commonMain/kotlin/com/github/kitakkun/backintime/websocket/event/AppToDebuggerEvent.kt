package com.github.kitakkun.backintime.websocket.event

import com.github.kitakkun.backintime.websocket.event.model.MethodInfo
import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppToDebuggerEvent {
    @Serializable
    data object Ping : AppToDebuggerEvent

    @Serializable
    data class RegisterInstance(
        val instanceId: String,
        val className: String,
    ) : AppToDebuggerEvent

    @Serializable
    data class RegisterClassInfo(
        val className: String,
        val superClassName: String,
        val methods: List<MethodInfo>,
        val properties: List<PropertyInfo>,
    ) : AppToDebuggerEvent

    @Serializable
    data class MethodCall(
        val instanceId: String,
        val methodName: String,
        val arguments: List<String>,
    ) : AppToDebuggerEvent

    @Serializable
    data class UpdatePropertyValue(
        val instanceId: String,
        val propertyName: String,
        val newValue: String,
    ) : AppToDebuggerEvent

    @Serializable
    data class UpdateInstanceState(
        val instanceId: String,
        val alive: Boolean,
    ) : AppToDebuggerEvent
}
