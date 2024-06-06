@file:Suppress("UNUSED")

package com.github.kitakkun.backintime.runtime.internal

import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException

@BackInTimeCompilerInternalApi
internal inline fun throwTypeMismatchException(
    propertyName: String,
    expectedType: String,
) {
    throw BackInTimeRuntimeException.TypeMismatchException(
        propertyName = propertyName,
        expectedType = expectedType,
    )
}

@BackInTimeCompilerInternalApi
internal inline fun throwNoSuchPropertyException(
    propertyName: String,
    parentClassFqName: String,
) {
    throw BackInTimeRuntimeException.NoSuchPropertyException(
        propertyName = propertyName,
        parentClassFqName = parentClassFqName,
    )
}