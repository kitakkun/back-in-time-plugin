package com.github.kitakkun.back_in_time.annotations

data class ValueChangeData(
    val instanceId: IdentityHashCode,
    val paramKey: String,
    val value: String,
)
