package com.github.kitakkun.backintime.debugger.feature.settings

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class SettingsTabScreenModel(
    private val settingsRepository: SettingsRepository,
) : ScreenModel {
    val bindModel = combine(
        settingsRepository.websocketPortFlow,
        settingsRepository.deleteSessionDataOnDisconnectFlow,
    ) { webSocketPort, deleteSessionDataOnDisconnect ->
        SettingsTabBindModel.Loaded(
            webSocketPort = webSocketPort,
            deleteSessionDataOnDisconnect = deleteSessionDataOnDisconnect,
        )
    }.stateIn(
        scope = screenModelScope,
        started = SharingStarted.Lazily,
        initialValue = SettingsTabBindModel.Loading,
    )

    fun updateWebSocketPort(webSocketPort: Int) {
        settingsRepository.webSocketPort = webSocketPort
    }

    fun updateDeleteSessionDataOnDisconnect(deleteSessionDataOnDisconnect: Boolean) {
        settingsRepository.deleteSessionDataOnDisconnect = deleteSessionDataOnDisconnect
    }
}
