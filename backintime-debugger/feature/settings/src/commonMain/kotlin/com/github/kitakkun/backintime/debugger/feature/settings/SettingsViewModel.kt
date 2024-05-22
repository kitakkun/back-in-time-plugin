package com.github.kitakkun.backintime.debugger.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel
class SettingsViewModel : ViewModel(), KoinComponent {
    private val settingsRepository: SettingsRepository by inject()

    val bindModel = combine(
        settingsRepository.websocketPortFlow,
        settingsRepository.deleteSessionDataOnDisconnectFlow,
    ) { webSocketPort, deleteSessionDataOnDisconnect ->
        SettingsBindModel.Loaded(
            webSocketPort = webSocketPort,
            deleteSessionDataOnDisconnect = deleteSessionDataOnDisconnect,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = SettingsBindModel.Loading,
    )

    fun updateWebSocketPort(webSocketPort: Int) {
        settingsRepository.webSocketPort = webSocketPort
    }

    fun updateDeleteSessionDataOnDisconnect(deleteSessionDataOnDisconnect: Boolean) {
        settingsRepository.deleteSessionDataOnDisconnect = deleteSessionDataOnDisconnect
    }
}
