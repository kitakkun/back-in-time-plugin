package com.kitakkun.backintime.tooling.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class Property(
    val signature: String,
    val type: String,
    val valueType: String,
    val isDebuggable: Boolean,
    val isBackInTimeDebuggableInstance: Boolean,
) {
    val name: String get() = signature.split(".").last()
}
