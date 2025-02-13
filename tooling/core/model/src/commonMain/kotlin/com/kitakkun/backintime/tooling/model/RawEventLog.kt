package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

@JsExport
data class RawEventLog(
    val eventId: String,
    val time: String,
    val label: String,
    val payload: Any,
)
