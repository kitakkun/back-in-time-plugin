package com.github.kitakkun.backintime.runtime

sealed interface BackInTimeDebugServiceEvent {
    val priority: Int

    data class RegisterInstance(
        val instance: BackInTimeDebuggable,
        val info: InstanceInfo,
        override val priority: Int = 0,
    ) : BackInTimeDebugServiceEvent

    data class RegisterRelationShip(
        val parentInstance: BackInTimeDebuggable,
        val childInstance: BackInTimeDebuggable,
        override val priority: Int = 1,
    ) : BackInTimeDebugServiceEvent

    data class MethodCall(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val methodName: String,
        override val priority: Int = 1,
    ) : BackInTimeDebugServiceEvent

    data class PropertyValueChange(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val propertyName: String,
        val propertyValue: Any?,
        override val priority: Int = 2,
    ) : BackInTimeDebugServiceEvent
}
