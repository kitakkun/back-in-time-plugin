package com.kitakkun.backintime.tooling.model

import kotlinx.serialization.Serializable
import kotlin.js.JsExport

@JsExport
@Serializable
data class ClassInfo(
    val classSignature: String,
    val superClassSignature: String,
    val properties: List<PropertyInfo>,
)
