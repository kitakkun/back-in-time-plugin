@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.event.BackInTimeDebuggableInstanceEvent
import com.github.kitakkun.backintime.runtime.getBackInTimeDebugService
import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo

@BackInTimeCompilerInternalApi
internal fun reportInstanceRegistration(
    instance: BackInTimeDebuggable,
    className: String,
    superClassName: String,
    properties: List<PropertyInfo>,
) = getBackInTimeDebugService().processInstanceEvent(
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
    ownerClassFqName: String,
    methodInvocationId: String,
    methodName: String,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.MethodCall(
        instance = instance,
        methodCallId = methodInvocationId,
        methodName = methodName,
        ownerClassFqName = ownerClassFqName,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportPropertyValueChange(
    instance: BackInTimeDebuggable,
    ownerClassFqName: String,
    methodInvocationId: String,
    propertyFqName: String,
    propertyValue: Any?,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.PropertyValueChange(
        instance = instance,
        methodCallId = methodInvocationId,
        propertyName = propertyFqName,
        propertyValue = propertyValue,
        ownerClassFqName = ownerClassFqName,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportNewRelationship(
    parentInstance: BackInTimeDebuggable,
    childInstance: BackInTimeDebuggable,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.RegisterRelationShip(
        parentInstance = parentInstance,
        childInstance = childInstance,
    ),
)
