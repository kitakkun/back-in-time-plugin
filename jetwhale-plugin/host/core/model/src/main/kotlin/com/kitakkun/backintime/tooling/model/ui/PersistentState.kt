package com.kitakkun.backintime.tooling.model.ui

data class PersistentState(
    val showNonDebuggableProperty: Boolean,
) {
    companion object {
        val Default = PersistentState(true)
    }
}
