package com.kitakkun.backintime.tooling.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class RawEventLog(
    val eventId: String,
    val time: String,
    val label: String,
    val payload: Any,
)
