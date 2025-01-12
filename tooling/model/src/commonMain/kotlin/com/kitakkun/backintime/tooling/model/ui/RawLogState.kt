package com.kitakkun.backintime.tooling.model.ui

import com.kitakkun.backintime.tooling.model.RawEventLog
import kotlin.js.JsExport

@JsExport
data class RawLogState(
    val logs: List<RawEventLog>,
    val selectedLogId: String?,
)
