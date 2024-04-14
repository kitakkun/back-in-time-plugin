package com.github.kitakkun.backintime.debugger.root

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootScreenModel(
    private val settingsRepository: SettingsRepository,
) : ScreenModel, KoinComponent {
    private val server: BackInTimeDebuggerService by inject()

    private val mutableState = mutableStateOf(RootState())
    val state: State<RootState> = mutableState

    fun startServer() {
        server.start(host = "localhost", port = settingsRepository.webSocketPort)
        mutableState.value = state.value.copy(isServerRunning = true)
    }
}
