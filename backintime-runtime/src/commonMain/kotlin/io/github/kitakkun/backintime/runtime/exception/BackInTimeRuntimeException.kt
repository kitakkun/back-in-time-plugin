package io.github.kitakkun.backintime.runtime.exception

sealed class BackInTimeRuntimeException(message: String) : RuntimeException(message) {
    class NoSuchPropertyException(parentClassFqName: String, propertyName: String) :
        BackInTimeRuntimeException("$parentClassFqName doesn't have a property with name: $propertyName")

    class TypeMismatchException(propertyName: String, expectedType: String) : BackInTimeRuntimeException("Type mismatch for property $propertyName: expected $expectedType")
}
