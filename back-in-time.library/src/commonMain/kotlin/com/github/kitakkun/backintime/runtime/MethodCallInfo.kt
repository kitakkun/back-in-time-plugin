package com.github.kitakkun.backintime.runtime

data class MethodCallInfo(
    val instanceUUID: UUIDString,
    val methodName: String,
    val methodCallUUID: String,
    val calledAt: Long,
)
