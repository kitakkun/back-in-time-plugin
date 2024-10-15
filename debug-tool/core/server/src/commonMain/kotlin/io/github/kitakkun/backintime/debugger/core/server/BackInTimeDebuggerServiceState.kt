package io.github.kitakkun.backintime.debugger.core.server

sealed class BackInTimeDebuggerServiceState {
    data object Uninitialized : BackInTimeDebuggerServiceState()
    data class Error(val error: Throwable) : BackInTimeDebuggerServiceState()
    data class Running(val host: String, val port: Int) : BackInTimeDebuggerServiceState()
}
