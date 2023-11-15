package com.github.kitakkun.backintime.runtime

data class ValueChangeData(
    val instanceUUID: UUIDString,
    val propertyName: String,
    val value: Any?,
    val valueType: String,
)
