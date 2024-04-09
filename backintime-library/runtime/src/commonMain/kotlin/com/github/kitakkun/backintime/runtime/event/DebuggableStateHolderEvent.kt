package com.github.kitakkun.backintime.runtime.event

import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo

/**
 * events inside debuggableStateHolder
 */
sealed interface DebuggableStateHolderEvent {
    /**
     * Register an instance to the debugService
     * @param instance the reference to the instance
     * @param className the FqName of the class
     * @param superClassName the FqName of the super class
     * @param properties the list of property info
     */
    data class RegisterInstance(
        val instance: BackInTimeDebuggable,
        val className: String,
        val superClassName: String,
        val properties: List<PropertyInfo>,
    ) : DebuggableStateHolderEvent

    /**
     * Register a relationship between parent and child
     * child is a property of parent
     * @param parentInstance the reference to the parent instance
     * @param childInstance the reference to the child instance
     */
    data class RegisterRelationShip(
        val parentInstance: BackInTimeDebuggable,
        val childInstance: BackInTimeDebuggable,
    ) : DebuggableStateHolderEvent

    /**
     * Notify that a method is called
     * @param instance the reference to the instance
     * @param methodCallId the unique id of the method call
     * @param methodName the name of the method
     */
    data class MethodCall(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val methodName: String,
    ) : DebuggableStateHolderEvent

    /**
     * Notify that a property value is changed
     * @param instance the reference to the instance
     * @param methodCallId the unique id of the method call
     * @param propertyName the name of the property
     * @param propertyValue the value of the property
     */
    data class PropertyValueChange(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val propertyName: String,
        val propertyValue: Any?,
    ) : DebuggableStateHolderEvent
}
