package io.github.kitakkun.backintime.debugger.core.model

data class SessionInfo(
    val id: String,
    val label: String?,
    val createdAt: Long,
    val isActive: Boolean,
)
