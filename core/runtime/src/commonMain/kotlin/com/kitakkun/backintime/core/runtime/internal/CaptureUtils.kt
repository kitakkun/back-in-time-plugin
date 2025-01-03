@file:Suppress("UNUSED")

package com.kitakkun.backintime.core.runtime.internal

import com.kitakkun.backintime.core.runtime.BackInTimeDebuggable

@BackInTimeCompilerInternalApi
internal inline fun <reified T : Any?> captureThenReturnValue(
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
