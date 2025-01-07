package com.kitakkun.backintime.tooling.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class InstanceInfo(
    val uuid: String,
    val classSignature: String,
    val alive: Boolean,
    val registeredAt: Int,
)
