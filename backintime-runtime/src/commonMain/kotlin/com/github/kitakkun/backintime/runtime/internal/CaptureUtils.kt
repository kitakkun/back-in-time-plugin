@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.BackInTimeDebuggable

@BackInTimeCompilerInternalApi
internal fun <T : Any> captureThenReturnValue(
    instance: BackInTimeDebuggable,
    methodInvocationId: String,
    propertyFqName: String,
    propertyValue: T,
): T {
    reportPropertyValueChange(
        instance = instance,
        methodInvocationId = methodInvocationId,
        propertyFqName = propertyFqName,
        propertyValue = propertyValue,
    )
    return propertyValue
}
