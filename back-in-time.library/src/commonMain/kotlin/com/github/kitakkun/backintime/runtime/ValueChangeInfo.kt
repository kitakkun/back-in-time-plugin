package com.github.kitakkun.backintime.runtime

data class ValueChangeInfo(
    val instanceUUID: UUIDString,
    val propertyName: String,
    val value: String,
    val valueType: String,
    val methodCallUUID: String,
)
