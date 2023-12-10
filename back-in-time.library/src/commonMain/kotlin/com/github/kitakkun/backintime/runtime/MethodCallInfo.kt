package com.github.kitakkun.backintime.runtime

import kotlinx.serialization.Serializable

@Serializable
data class MethodCallInfo(
    val instanceUUID: String,
    val methodName: String,
    val methodCallUUID: String,
    val calledAt: Long,
)
