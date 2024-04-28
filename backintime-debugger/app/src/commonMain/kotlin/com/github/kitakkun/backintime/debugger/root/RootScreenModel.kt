package com.github.kitakkun.backintime.debugger.root

import cafe.adriel.voyager.core.model.ScreenModel
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerServiceState
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootScreenModel(
    private val settingsRepository: SettingsRepository,
) : ScreenModel, KoinComponent {
    private val service: BackInTimeDebuggerService by inject()
    val serverState: StateFlow<BackInTimeDebuggerServiceState> = service.serviceStateFlow

    fun startServer() {
        service.start(host = "localhost", port = settingsRepository.webSocketPort)
    }
}
