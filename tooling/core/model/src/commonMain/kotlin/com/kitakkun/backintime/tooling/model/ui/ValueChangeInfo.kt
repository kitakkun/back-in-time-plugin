package com.kitakkun.backintime.tooling.model.ui

import kotlin.js.JsExport

@JsExport
data class ValueChangeInfo(
    val methodCallUUID: String,
    val time: Int,
    val value: String,
)