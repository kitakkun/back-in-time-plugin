@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable

@BackInTimeCompilerInternalApi
internal fun <T : Any> captureThenReturnValue(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertyName: String,
    propertyValue: T,
    className: String,
): T {
    reportPropertyValueChange(
        instance = instance,
        methodInvocationId = methodInvocationId,
        propertyName = propertyName,
        propertyValue = propertyValue,
        className = className,
    )
    return propertyValue
}
