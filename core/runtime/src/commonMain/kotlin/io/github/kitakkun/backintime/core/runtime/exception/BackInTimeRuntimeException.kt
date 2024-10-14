package io.github.kitakkun.backintime.core.runtime.exception

sealed class BackInTimeRuntimeException(message: String) : Throwable(message) {
    class NoSuchPropertyException(parentClassFqName: String, propertyName: String) :
        BackInTimeRuntimeException("$parentClassFqName doesn't have a property with name: $propertyName")

    class TypeMismatchException(propertyName: String, expectedType: String) : BackInTimeRuntimeException("Type mismatch for property $propertyName: expected $expectedType")
}
