package com.kitakkun.backintime.tooling.flipper.selector

import com.kitakkun.backintime.tooling.model.ui.HistoryInfo

@JsExport
data class BackInTimePropertyInfo(
    val signature: String,
    val jsonValue: String,
)

@JsExport
fun selectBackInTimeValues(
    histories: List<HistoryInfo>,
    index: Int,
): List<BackInTimePropertyInfo> {
    return histories
        .slice(0..(index + 1))
        .filterIsInstance<HistoryInfo.MethodCallHistoryInfo>()
        .flatMap { it.valueChanges }
        .reversed()
        .distinctBy { it.propertySignature }
        .map {
            BackInTimePropertyInfo(
                signature = it.propertySignature,
                jsonValue = it.value,
            )
        }
}