package com.kitakkun.backintime.tooling.model

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class PropertyInfo(
    val signature: String,
    val debuggable: Boolean,
    val isDebuggableStateHolder: Boolean,
    val propertyType: String,
    val valueType: String,
) {
    val name: String get() = signature.split(".").last()
}
