package com.github.kitakkun.backintime.debugger.feature.settings

sealed interface SettingsTabBindModel {
    data object Loading : SettingsTabBindModel
    data class Loaded(
        val webSocketPort: Int,
        val deleteSessionDataOnDisconnect: Boolean,
    ) : SettingsTabBindModel
}
