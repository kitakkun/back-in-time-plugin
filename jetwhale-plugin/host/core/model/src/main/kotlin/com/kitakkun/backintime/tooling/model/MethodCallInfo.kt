package com.kitakkun.backintime.tooling.model

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

data class ValueChangeInfo(
    val propertySignature: String,
    val value: String,
)
