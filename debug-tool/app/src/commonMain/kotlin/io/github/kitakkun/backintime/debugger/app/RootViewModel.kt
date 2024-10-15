package io.github.kitakkun.backintime.debugger.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.kitakkun.backintime.debugger.core.data.SessionInfoRepository
import io.github.kitakkun.backintime.debugger.core.datastore.BackInTimePreferences
import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerService
import io.github.kitakkun.backintime.debugger.core.server.BackInTimeDebuggerServiceState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinViewModel
class RootViewModel : ViewModel(), KoinComponent {
    private val settingsRepository: BackInTimePreferences by inject()
    private val service: BackInTimeDebuggerService by inject()
    private val sessionInfoRepository: SessionInfoRepository by inject()

    init {
        viewModelScope.launch {
            sessionInfoRepository.markAllAsDisconnected()
        }
    }

    val serverState: StateFlow<BackInTimeDebuggerServiceState> = service.serviceStateFlow

    fun startServer() {
//        service.start(host = "localhost", port = settingsRepository.webSocketPort)
    }
}
