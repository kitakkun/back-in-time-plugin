package com.github.kitakkun.backintime.runtime

import java.util.UUID

data class BackInTimeParentMethodCallInfo(
    val signature: String,
    val calledTimeMillis: Long = System.currentTimeMillis(),
    val callUuid: String = UUID.randomUUID().toString(),
)
