package com.kitakkun.backintime.tooling.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
data class MethodCallInfo(
    val callUUID: String,
    val instanceUUID: String,
    val methodSignature: String,
    val calledAt: Int,
    val valueChanges: List<ValueChangeInfo>,
)

@OptIn(ExperimentalJsExport::class)
@JsExport
data class ValueChangeInfo(
    val propertySignature: String,
    val value: String,
)
