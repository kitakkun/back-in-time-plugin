@file:Suppress("UNUSED")

package com.kitakkun.backintime.core.runtime.internal

import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.core.runtime.backInTimeJson
import com.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent
import com.kitakkun.backintime.core.runtime.getBackInTimeDebugService
import com.kitakkun.backintime.core.websocket.event.model.PropertyInfo
import kotlinx.serialization.encodeToString

@BackInTimeCompilerInternalApi
internal fun reportInstanceRegistration(
    instance: BackInTimeDebuggable,
    className: String,
    superClassName: String,
    properties: List<PropertyInfo>,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.RegisterTarget(
        instance = instance,
        classSignature = className,
        superClassSignature = superClassName,
        properties = properties,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportMethodInvocation(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    methodName: String,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.MethodCall(
        instance = instance,
        methodCallId = methodInvocationId,
        methodSignature = methodName,
    ),
)

@BackInTimeCompilerInternalApi
internal inline fun <reified T> reportPropertyValueChange(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertyFqName: String,
    propertyValue: T,
) {
    val service = getBackInTimeDebugService()
    try {
        service.processInstanceEvent(
            BackInTimeDebuggableInstanceEvent.PropertyValueChange(
                instance = instance,
                methodCallId = methodInvocationId,
                propertySignature = propertyFqName,
                propertyValue = backInTimeJson.encodeToString(propertyValue),
            ),
        )
    } catch (e: Throwable) {
        service.processInstanceEvent(BackInTimeDebuggableInstanceEvent.Error(e))
    }
}

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
