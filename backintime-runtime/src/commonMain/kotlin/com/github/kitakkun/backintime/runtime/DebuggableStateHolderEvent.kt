package com.github.kitakkun.backintime.runtime

sealed interface DebuggableStateHolderEvent {
    val priority: Int

    data class RegisterInstance(
        val instance: BackInTimeDebuggable,
        val info: InstanceInfo,
        override val priority: Int = 0,
    ) : DebuggableStateHolderEvent

    data class RegisterRelationShip(
        val parentInstance: BackInTimeDebuggable,
        val childInstance: BackInTimeDebuggable,
        override val priority: Int = 1,
    ) : DebuggableStateHolderEvent

    data class MethodCall(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val methodName: String,
        override val priority: Int = 1,
    ) : DebuggableStateHolderEvent

    data class PropertyValueChange(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val propertyName: String,
        val propertyValue: Any?,
        override val priority: Int = 2,
    ) : DebuggableStateHolderEvent
}
