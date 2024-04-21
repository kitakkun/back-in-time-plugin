package com.github.kitakkun.backintime.debugger.feature.connection

sealed interface ConnectionTabBindModel {
    data object Loading : ConnectionTabBindModel
    data object ServerNotStarted : ConnectionTabBindModel
    data class ServerRunning(
        val host: String,
        val port: Int,
        val sessionBindModels: List<SessionBindModel>,
    ) : ConnectionTabBindModel
}
