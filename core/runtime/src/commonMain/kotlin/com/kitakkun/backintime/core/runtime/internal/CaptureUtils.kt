@file:Suppress("UNUSED")

package com.kitakkun.backintime.core.runtime.internal

import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable

@BackInTimeCompilerInternalApi
internal inline fun <reified T : Any?> captureThenReturnValue(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertySignature: String,
    propertyValue: T,
): T {
    reportPropertyValueChange(
        instance = instance,
        methodInvocationId = methodInvocationId,
        propertySignature = propertySignature,
        propertyValue = propertyValue,
    )
    return propertyValue
}
