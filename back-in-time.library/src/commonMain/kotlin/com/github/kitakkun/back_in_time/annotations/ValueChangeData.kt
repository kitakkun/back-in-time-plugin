package com.github.kitakkun.back_in_time.annotations

data class ValueChangeData(
    val instanceUUID: UUIDString,
    val paramKey: String,
    val value: String,
)
