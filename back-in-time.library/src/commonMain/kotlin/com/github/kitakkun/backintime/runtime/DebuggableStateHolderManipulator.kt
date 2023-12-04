package com.github.kitakkun.backintime.runtime

import com.github.kitakkun.backintime.runtime.exception.BackInTimeRuntimeException

interface DebuggableStateHolderManipulator {
    @Throws(BackInTimeRuntimeException.NullValueNotAssignableException::class, BackInTimeRuntimeException.NoSuchPropertyException::class)
    fun forceSetPropertyValueForBackInTimeDebug(propertyName: String, value: Any?)
}
