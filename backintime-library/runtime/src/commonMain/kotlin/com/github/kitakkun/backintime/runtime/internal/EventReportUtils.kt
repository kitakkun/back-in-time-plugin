@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable
import com.github.kitakkun.backintime.runtime.event.DebuggableStateHolderEvent
import com.github.kitakkun.backintime.websocket.event.model.PropertyInfo

@BackInTimeCompilerInternalApi
internal inline fun reportInstanceRegistration(
    instance: BackInTimeDebuggable,
    className: String,
    superClassName: String,
    properties: List<PropertyInfo>,
) = BackInTimeDebugService.emitEvent(
    DebuggableStateHolderEvent.RegisterInstance(
        instance = instance,
        className = className,
        superClassName = superClassName,
        properties = properties,
    ),
)

@BackInTimeCompilerInternalApi
internal inline fun reportMethodInvocation(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    methodName: String,
    className: String,
) = BackInTimeDebugService.emitEvent(
    DebuggableStateHolderEvent.MethodCall(
        instance = instance,
        methodCallId = methodInvocationId,
        methodName = methodName,
        className = className,
    ),
)

@BackInTimeCompilerInternalApi
internal inline fun reportPropertyValueChange(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertyName: String,
    propertyValue: Any?,
    className: String,
) = BackInTimeDebugService.emitEvent(
    DebuggableStateHolderEvent.PropertyValueChange(
        instance = instance,
        methodCallId = methodInvocationId,
        propertyName = propertyName,
        propertyValue = propertyValue,
        className = className,
    ),
)

@BackInTimeCompilerInternalApi
internal inline fun reportNewRelationship(
    parentInstance: BackInTimeDebuggable,
    childInstance: BackInTimeDebuggable,
) = BackInTimeDebugService.emitEvent(
    DebuggableStateHolderEvent.RegisterRelationShip(
        parentInstance = parentInstance,
        childInstance = childInstance,
    ),
)
