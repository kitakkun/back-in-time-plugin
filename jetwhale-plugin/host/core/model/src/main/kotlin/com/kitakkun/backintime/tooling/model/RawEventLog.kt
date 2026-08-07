package com.kitakkun.backintime.tooling.model

data class RawEventLog(
    val eventId: String,
    val time: String,
    val label: String,
    val payload: Any,
)
