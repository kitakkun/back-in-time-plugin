package com.kitakkun.backintime.tooling.model.ui

import kotlin.js.JsExport

@JsExport
data class PropertyItem(
    val name: String,
    val signature: String,
    val type: String,
    val debuggable: Boolean,
    val eventCount: Int,
    val stateHolderInstance: InstanceItem?,
)
