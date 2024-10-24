@file:Suppress("UNUSED")

package io.github.kitakkun.backintime.core.runtime.internal

import io.github.kitakkun.backintime.core.runtime.BackInTimeDebuggable

@BackInTimeCompilerInternalApi
internal fun <T : Any> captureThenReturnValue(
    instance: BackInTimeDebuggable,
    ownerClassFqName: String,
    methodInvocationId: String,
    propertyFqName: String,
    propertyValue: T,
): T {
    reportPropertyValueChange(
        instance = instance,
        methodInvocationId = methodInvocationId,
        propertyFqName = propertyFqName,
        propertyValue = propertyValue,
        ownerClassFqName = ownerClassFqName,
    )
    return propertyValue
}
