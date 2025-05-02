package com.kitakkun.backintime.core.runtime.event

import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import kotlinx.datetime.Clock

/**
 * events inside the BackInTimeDebuggable instance
 */
sealed class BackInTimeDebuggableInstanceEvent {
    val time: Long = Clock.System.now().toEpochMilliseconds()

    /**
     * Register an instance to the debugService
     * @param instance the reference to the instance
     * @param classSignature the signature of the class (same as classId)
     * @param superClassSignature the signature of the super class (same as classId)
     * @param properties the list of property info
     */
    data class RegisterTarget(
        val instance: BackInTimeDebuggable,
        val classSignature: String,
        val superClassSignature: String,
        val properties: List<String>,
    ) : BackInTimeDebuggableInstanceEvent()

    /**
     * Register a relationship between parent and child
     * child is a property of parent
     * @param parentInstance the reference to the parent instance
     * @param childInstance the reference to the child instance
     */
    data class RegisterRelationShip(
        val parentInstance: BackInTimeDebuggable,
        val childInstance: BackInTimeDebuggable,
    ) : BackInTimeDebuggableInstanceEvent()

    /**
     * Notify that a method is called
     * @param instance the reference to the instance
     * @param methodCallId the unique id of the method call
     * @param methodSignature the signature of the method. See [com.kitakkun.backintime.compiler.backend.utils.signatureForBackInTimeDebugger] for details.
     */
    data class MethodCall(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val methodSignature: String,
    ) : BackInTimeDebuggableInstanceEvent()

    /**
     * Notify that a property value is changed
     * @param instance the reference to the instance
     * @param methodCallId the unique id of the method call
     * @param propertySignature the signature of the property. See [com.kitakkun.backintime.compiler.backend.utils.signatureForBackInTimeDebugger] for details.
     * @param propertyValue the serialized value of the property
     */
    data class PropertyValueChange(
        val instance: BackInTimeDebuggable,
        val methodCallId: String,
        val propertySignature: String,
        val propertyValue: String,
    ) : BackInTimeDebuggableInstanceEvent()

    data class Error(
        val exception: Throwable
    ) : BackInTimeDebuggableInstanceEvent()
}
