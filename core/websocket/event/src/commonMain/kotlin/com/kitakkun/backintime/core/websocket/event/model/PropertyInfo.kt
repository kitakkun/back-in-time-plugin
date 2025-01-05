package com.kitakkun.backintime.core.websocket.event.model

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
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
