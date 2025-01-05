package com.kitakkun.backintime.tooling.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class ClassInfo(
    val classSignature: String,
    val superClassSignature: String,
    val properties: List<PropertyInfo>,
)
