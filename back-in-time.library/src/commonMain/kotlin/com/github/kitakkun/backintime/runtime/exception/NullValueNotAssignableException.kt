package com.github.kitakkun.backintime.runtime.exception

sealed class BackInTimeRuntimeException(message: String) : RuntimeException(message) {
    class NullValueNotAssignableException(propertyName: String, propertyType: String) :
        BackInTimeRuntimeException("Cannot assign null value to $propertyName: $propertyType")

    class NoSuchPropertyException(parentClassFqName: String, propertyName: String) :
        BackInTimeRuntimeException("${parentClassFqName} doesn't have a property with name: $propertyName")

    class InvalidJsonInputException(json: String) : BackInTimeRuntimeException("Failed to parse json: $json")
}
