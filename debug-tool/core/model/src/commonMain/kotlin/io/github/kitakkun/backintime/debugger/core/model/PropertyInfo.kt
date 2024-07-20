package io.github.kitakkun.backintime.debugger.core.model

data class PropertyInfo(
    val name: String,
    val type: String,
    val debuggable: Boolean,
    val backInTimeDebuggable: Boolean,
)
