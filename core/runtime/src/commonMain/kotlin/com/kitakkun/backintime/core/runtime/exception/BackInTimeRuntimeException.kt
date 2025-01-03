package com.kitakkun.backintime.core.runtime.exception

sealed class BackInTimeRuntimeException(message: String) : Throwable(message) {
    class NoSuchPropertyException(parentClassFqName: String, propertyName: String) :
        BackInTimeRuntimeException("$parentClassFqName doesn't have a property with name: $propertyName")
}
