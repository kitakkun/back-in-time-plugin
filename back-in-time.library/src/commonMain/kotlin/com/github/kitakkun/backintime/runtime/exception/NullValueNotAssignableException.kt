package com.github.kitakkun.backintime.runtime.exception

sealed class BackInTimeRuntimeException(message: String) : Exception(message) {
    class NullValueNotAssignableException(propertyName: String, propertyType: String) :
        BackInTimeRuntimeException("Cannot assign null value to $propertyName: $propertyType")

    class NoSuchPropertyException(propertyName: String) : BackInTimeRuntimeException("No such property: $propertyName")

    class InvalidJsonInputException(json: String) : BackInTimeRuntimeException("Failed to parse json: $json")
}
