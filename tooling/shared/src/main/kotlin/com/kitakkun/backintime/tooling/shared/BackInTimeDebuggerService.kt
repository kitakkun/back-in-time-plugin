package com.kitakkun.backintime.tooling.shared

interface BackInTimeDebuggerService {
    data class State(
        val serverIsRunning: Boolean,
        val connections: List<String>,
    )

    val state: State

    fun restartServer(port: Int)
}
