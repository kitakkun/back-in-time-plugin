package io.github.kitakkun.backintime.debugger.core.model

data class ValueChange(
    val propertyOwnerClassName: String,
    val propertyName: String,
    val newValue: String,
)
