package com.github.kitakkun.backintime.debugger.feature.settings

sealed interface SettingsBindModel {
    data object Loading : SettingsBindModel
    data class Loaded(
        val webSocketPort: Int,
        val deleteSessionDataOnDisconnect: Boolean,
    ) : SettingsBindModel
}
