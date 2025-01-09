package com.kitakkun.backintime.tooling.model.ui

import kotlin.js.JsExport

@JsExport
data class BackInTimeState(
    val open: Boolean,
    val histories: List<HistoryInfo>,
    val instanceUUID: String,
)
