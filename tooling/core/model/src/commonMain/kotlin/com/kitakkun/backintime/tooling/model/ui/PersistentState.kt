package com.kitakkun.backintime.tooling.model.ui

import kotlin.js.JsExport

@JsExport
data class PersistentState(
    val showNonDebuggableProperty: Boolean,
) {
    companion object {
        val Default = PersistentState(true)
    }
}
