@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent
import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo

@BackInTimeCompilerInternalApi
internal fun reportInstanceRegistration(
    instance: BackInTimeDebuggable,
    className: String,
    superClassName: String,
    properties: List<PropertyInfo>,
) = BackInTimeDebugService.emitEvent(
    BackInTimeDebuggableInstanceEvent.RegisterTarget(
        instance = instance,
        className = className,
        superClassName = superClassName,
        properties = properties,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportMethodInvocation(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    methodName: String,
) = BackInTimeDebugService.emitEvent(
    BackInTimeDebuggableInstanceEvent.MethodCall(
        instance = instance,
        methodCallId = methodInvocationId,
        methodName = methodName,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportPropertyValueChange(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertyName: String,
    propertyValue: Any?,
) = BackInTimeDebugService.emitEvent(
    BackInTimeDebuggableInstanceEvent.PropertyValueChange(
        instance = instance,
        methodCallId = methodInvocationId,
        propertyName = propertyName,
        propertyValue = propertyValue,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportNewRelationship(
    parentInstance: BackInTimeDebuggable,
    childInstance: BackInTimeDebuggable,
) = BackInTimeDebugService.emitEvent(
    BackInTimeDebuggableInstanceEvent.RegisterRelationShip(
        parentInstance = parentInstance,
        childInstance = childInstance,
    ),
)
