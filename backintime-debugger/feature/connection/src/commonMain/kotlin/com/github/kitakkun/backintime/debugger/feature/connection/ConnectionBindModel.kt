package com.github.kitakkun.backintime.debugger.feature.connection

sealed interface ConnectionBindModel {
    data object Loading : ConnectionBindModel
    data object ServerNotStarted : ConnectionBindModel
    data class ServerRunning(
        val host: String,
        val port: Int,
        val sessionBindModels: List<SessionBindModel>,
    ) : ConnectionBindModel

    data class ServerError(private val error: Throwable) : ConnectionBindModel
}
