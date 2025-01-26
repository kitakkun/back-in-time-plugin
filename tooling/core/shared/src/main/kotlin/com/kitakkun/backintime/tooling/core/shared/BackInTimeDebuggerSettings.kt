package com.kitakkun.backintime.tooling.core.shared

interface BackInTimeDebuggerSettings {
    data class State(
        val serverPort: Int = 50023,
        val showNonDebuggableProperties: Boolean = true,
        val persistSessionData: Boolean = false,
        val databasePath: String? = null,
    )

    fun getState(): State
    fun loadState(state: State)

    fun updateServerPort(port: Int) {
        loadState(getState().copy(serverPort = port))
    }

    fun updateShowNonDebuggableProperties(showNonDebuggableProperties: Boolean) {
        loadState(getState().copy(showNonDebuggableProperties = showNonDebuggableProperties))
    }

    fun updateDatabasePath(path: String?) {
        loadState(getState().copy(databasePath = path))
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
