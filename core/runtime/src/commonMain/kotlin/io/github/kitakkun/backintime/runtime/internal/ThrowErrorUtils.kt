@file:Suppress("UNUSED")

package io.github.kitakkun.backintime.runtime.internal

import io.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException

@BackInTimeCompilerInternalApi
internal fun throwTypeMismatchException(
    propertyName: String,
    expectedType: String,
) {
    throw BackInTimeRuntimeException.TypeMismatchException(
        propertyName = propertyName,
        expectedType = expectedType,
    )
}

@BackInTimeCompilerInternalApi
internal fun throwNoSuchPropertyException(
    propertyName: String,
    parentClassFqName: String,
) {
    throw BackInTimeRuntimeException.NoSuchPropertyException(
        propertyName = propertyName,
        parentClassFqName = parentClassFqName,
    )
}
