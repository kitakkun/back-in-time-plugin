package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

@JsExport
data class ClassInfo(
    val classSignature: String,
    val superClassSignature: String,
    val properties: List<PropertyInfo>,
)
