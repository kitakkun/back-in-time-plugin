package io.github.kitakkun.backintime.debugger.core.model

data class Session(
    val id: String,
    val instances: List<Instance>,
    val logs: List<EventLog>,
    val createdAt: Long,
)
