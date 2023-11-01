package com.github.kitakkun.backintime.runtime

interface DebuggableStateHolderManipulator {
    fun forceSetPropertyValueForBackInTimeDebug(propertyName: String, value: Any?)
}
