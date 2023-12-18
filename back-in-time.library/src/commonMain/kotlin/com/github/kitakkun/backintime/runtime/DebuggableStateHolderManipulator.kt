package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException
import kotlinx.serialization.SerializationException

interface DebuggableStateHolderManipulator {
    @Throws(BackInTimeRuntimeException.NullValueNotAssignableException::class, BackInTimeRuntimeException.NoSuchPropertyException::class)
    fun forceSetValue(propertyName: String, value: Any?)

    @Throws(BackInTimeRuntimeException.NoSuchPropertyException::class, SerializationException::class)
    fun serializeValue(propertyName: String, value: Any?): String

    @Throws(BackInTimeRuntimeException.NoSuchPropertyException::class, SerializationException::class)
    fun deserializeValue(propertyName: String, value: String): Any?
}
