package com.kitakkun.backintime.tooling.core.shared

import kotlinx.serialization.Serializable

interface BackInTimeDebuggerSettings {
    @Serializable
    data class State(
        val serverPort: Int = 50023,
        val showNonDebuggableProperties: Boolean = true,
        val persistSessionData: Boolean = false,
        val databasePath: String? = null,
    )

    fun getState(): State
    fun loadState(state: State)

    fun update(block: (prevState: State) -> State) {
        loadState(block(getState()))
    }

    companion object {
        val Dummy = object : BackInTimeDebuggerSettings {
            override fun loadState(state: State) {}

            override fun getState(): State {
                return State()
            }
        }
    }
}
