@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable

@BackInTimeCompilerInternalApi
internal inline fun <T : Any> captureThenReturnValue(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertyName: String,
    propertyValue: T,
): T {
    reportPropertyValueChange(
        instance = instance,
        methodInvocationId = methodInvocationId,
        propertyName = propertyName,
        propertyValue = propertyValue,
    )
    return propertyValue
}
