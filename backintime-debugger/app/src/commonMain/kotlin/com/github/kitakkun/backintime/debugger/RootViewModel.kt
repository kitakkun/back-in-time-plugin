package com.github.kitakkun.backintime.debugger

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerServiceState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootViewModel : ViewModel(), KoinComponent {
    private val settingsRepository: SettingsRepository by inject()
    private val service: BackInTimeDebuggerService by inject()
    private val sessionInfoRepository: SessionInfoRepository by inject()

    init {
        viewModelScope.launch {
            sessionInfoRepository.markAllAsDisconnected()
        }
    }

    val serverState: StateFlow<BackInTimeDebuggerServiceState> = service.serviceStateFlow

    fun startServer() {
        service.start(host = "localhost", port = settingsRepository.webSocketPort)
    }
}
