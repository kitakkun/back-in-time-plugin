package com.github.kitakkun.backintime.runtime

sealed interface DebuggableStateHolderEvent {
    data class RegisterInstance(
        val instance: BackInTimeDebuggable,
        val info: InstanceInfo,
    ) : DebuggableStateHolderEvent

    data class RegisterRelationShip(
        val parentInstance: BackInTimeDebuggable,
        val childInstance: BackInTimeDebuggable,
    ) : DebuggableStateHolderEvent

    data class MethodCall(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val methodName: String,
    ) : DebuggableStateHolderEvent

    data class PropertyValueChange(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val propertyName: String,
        val propertyValue: Any?,
    ) : DebuggableStateHolderEvent
}
