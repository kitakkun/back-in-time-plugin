package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

@JsExport
data class Property(
    val name: String,
    val type: String,
    val totalEvents: Int,
    val debuggable: Boolean,
)
