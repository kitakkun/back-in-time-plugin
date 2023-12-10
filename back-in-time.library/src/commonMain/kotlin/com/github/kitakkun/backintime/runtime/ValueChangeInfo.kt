package com.github.kitakkun.backintime.runtime

import kotlinx.serialization.Serializable

@Serializable
data class ValueChangeInfo(
    val instanceUUID: String,
    val propertyName: String,
    val value: String,
    val valueType: String,
    val methodCallUUID: String,
)
