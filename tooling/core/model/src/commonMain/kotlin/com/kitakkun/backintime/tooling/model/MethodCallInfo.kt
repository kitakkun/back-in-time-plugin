package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

@JsExport
data class MethodCallInfo(
    val callUUID: String,
    val instanceUUID: String,
    val methodSignature: String,
    val calledAt: Int,
    val valueChanges: List<ValueChangeInfo>,
) {
    @Suppress("UNUSED")
    fun copyWithAppendingNewValueChangeInfo(valueChangeInfo: ValueChangeInfo) = copy(valueChanges = this.valueChanges + valueChangeInfo)
}

@JsExport
data class ValueChangeInfo(
    val propertySignature: String,
    val value: String,
)
