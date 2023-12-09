package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException

interface DebuggableStateHolderManipulator {
    @Throws(BackInTimeRuntimeException.NullValueNotAssignableException::class, BackInTimeRuntimeException.NoSuchPropertyException::class)
    fun forceSetValue(propertyName: String, value: Any?)

    fun serializeValue(propertyName: String, value: Any?): String

    fun deserializeValue(propertyName: String, value: String): Any?
}
