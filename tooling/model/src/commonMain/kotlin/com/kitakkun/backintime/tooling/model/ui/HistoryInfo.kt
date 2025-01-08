@file:OptIn(ExperimentalJsExport::class)
@file:JsExport

package com.kitakkun.backintime.tooling.model.ui

import com.kitakkun.backintime.tooling.model.ValueChangeInfo
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

sealed class HistoryInfo {
    abstract val title: String
    abstract val subtitle: String
    abstract val timestamp: Int
    abstract val description: String

    data class RegisterHistoryInfo(
        override val subtitle: String,
        override val timestamp: Int,
        override val description: String,
    ) : HistoryInfo() {
        override val title: String = "register"
    }

    data class MethodCallHistoryInfo(
        override val subtitle: String,
        override val timestamp: Int,
        override val description: String,
        val valueChanges: List<ValueChangeInfo>,
    ) : HistoryInfo() {
        override val title: String = "methodCall"
    }
}
