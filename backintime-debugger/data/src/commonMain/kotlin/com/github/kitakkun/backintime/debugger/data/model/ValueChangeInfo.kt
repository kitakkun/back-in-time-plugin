package com.github.kitakkun.backintime.debugger.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ValueChangeInfo(
    val propertyName: String,
    val value: String,
)
