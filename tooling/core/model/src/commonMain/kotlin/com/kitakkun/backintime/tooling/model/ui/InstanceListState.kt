package com.kitakkun.backintime.tooling.model.ui

import kotlin.js.JsExport

@JsExport
data class InstanceListState(
    val instances: List<InstanceItem>,
    val showNonDebuggableProperty: Boolean,
)
