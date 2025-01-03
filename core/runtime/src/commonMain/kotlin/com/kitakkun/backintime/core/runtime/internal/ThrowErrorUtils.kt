@file:Suppress("UNUSED")

package com.kitakkun.backintime.core.runtime.internal

import com.kitakkun.backintime.core.runtime.exception.BackInTimeRuntimeException

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
