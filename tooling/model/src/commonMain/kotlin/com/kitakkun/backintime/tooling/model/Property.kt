package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

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
