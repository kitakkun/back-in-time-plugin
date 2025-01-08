@file:Suppress("UNUSED")

package com.kitakkun.backintime.core.runtime.internal

import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable
import com.kitakkun.backintime.core.runtime.backInTimeJson
import com.kitakkun.backintime.core.runtime.event.BackInTimeDebuggableInstanceEvent
import com.kitakkun.backintime.core.runtime.getBackInTimeDebugService
import com.kitakkun.backintime.tooling.model.PropertyInfo
import kotlinx.serialization.encodeToString

@BackInTimeCompilerInternalApi
internal fun reportInstanceRegistration(
    instance: BackInTimeDebuggable,
    classSignature: String,
    superClassSignature: String,
    properties: List<PropertyInfo>,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.RegisterTarget(
        instance = instance,
        classSignature = classSignature,
        superClassSignature = superClassSignature,
        properties = properties,
    ),
)

@BackInTimeCompilerInternalApi
internal fun reportMethodInvocation(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    methodSignature: String,
) = getBackInTimeDebugService().processInstanceEvent(
    BackInTimeDebuggableInstanceEvent.MethodCall(
        instance = instance,
        methodCallId = methodInvocationId,
        methodSignature = methodSignature,
    ),
)

@BackInTimeCompilerInternalApi
internal inline fun <reified T> reportPropertyValueChange(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertySignature: String,
    propertyValue: T,
) {
    val service = getBackInTimeDebugService()
    try {
        service.processInstanceEvent(
            BackInTimeDebuggableInstanceEvent.PropertyValueChange(
                instance = instance,
                methodCallId = methodInvocationId,
                propertySignature = propertySignature,
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
