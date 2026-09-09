package com.kitakkun.backintime.tooling.core.shared

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

interface BackInTimeDebuggerSettings {
    @Serializable
    data class State(
        val serverPort: Int = 50023,
        val showNonDebuggableProperties: Boolean = true,
        val persistSessionData: Boolean = false,
        val databasePath: String? = null,
    )

    val stateFlow: StateFlow<State>

    fun update(block: (prevState: State) -> State)

    companion object {
        val Dummy = object : BackInTimeDebuggerSettings {
            override val stateFlow: StateFlow<State> = MutableStateFlow(State())
            override fun update(block: (State) -> State) {}
        }
    }
}
