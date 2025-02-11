package com.kitakkun.backintime.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDatabase
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.logic.EventEffect
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.usecase.LocalDatabase
import com.kitakkun.backintime.tooling.model.Tab

sealed interface SettingsScreenEvent {
    data class UpdatePortNumber(val portNumber: Int) : SettingsScreenEvent
    data class UpdateNonDebuggablePropertyVisibility(val visible: Boolean) : SettingsScreenEvent
    data class ShowInspectorForSession(val sessionId: String) : SettingsScreenEvent
    data class ShowLogForSession(val sessionId: String) : SettingsScreenEvent
    data object RestartServer : SettingsScreenEvent
    data class RestartDatabaseWithFile(val databaseFilePath: String, val migrate: Boolean) : SettingsScreenEvent
    data class RestartDatabaseInMemory(val migrate: Boolean) : SettingsScreenEvent
}

@Composable
fun settingsScreenPresenter(eventEmitter: EventEmitter<SettingsScreenEvent>): SettingsScreenUiState {
    val pluginStateService = LocalPluginStateService.current
    val database = LocalDatabase.current
    val server = LocalServer.current
    val settings = LocalSettings.current

    val databaseState by database.stateFlow.collectAsState()
    val settingsState by rememberUpdatedState(settings.getState())
    val connections = server.state.connections

    EventEffect(eventEmitter) { event ->
        when (event) {
            is SettingsScreenEvent.UpdatePortNumber -> {
                settings.update {
                    it.copy(serverPort = event.portNumber)
                }
            }

            is SettingsScreenEvent.UpdateNonDebuggablePropertyVisibility -> {
                settings.update {
                    it.copy(showNonDebuggableProperties = event.visible)
                }
            }

            is SettingsScreenEvent.RestartServer -> {
                server.restartServer(settingsState.serverPort)
            }

            is SettingsScreenEvent.ShowInspectorForSession -> {
                pluginStateService.loadState(
                    state = pluginStateService.getState().let {
                        it.copy(
                            globalState = it.globalState.copy(
                                activeTab = Tab.Inspector,
                                selectedSessionId = event.sessionId,
                            )
                        )
                    }
                )
            }

            is SettingsScreenEvent.ShowLogForSession -> {
                pluginStateService.loadState(
                    state = pluginStateService.getState().let {
                        it.copy(
                            globalState = it.globalState.copy(
                                activeTab = Tab.Log,
                                selectedSessionId = event.sessionId,
                            )
                        )
                    }
                )
            }

            is SettingsScreenEvent.RestartDatabaseWithFile -> {
                database.restartDatabaseAsFile(
                    filePath = event.databaseFilePath,
                    migrate = event.migrate,
                )
                settings.update { it.copy(databasePath = event.databaseFilePath, persistSessionData = true) }
            }

            is SettingsScreenEvent.RestartDatabaseInMemory -> {
                database.restartDatabaseInMemory(migrate = event.migrate)
                settings.update { it.copy(persistSessionData = false) }
            }
        }
    }

    return SettingsScreenUiState(
        serverStatus = if (server.state.serverIsRunning && server.state.port != null) {
            SettingsScreenUiState.ServerStatus.Running(
                connections.size,
                server.state.port!!,
            )
        } else {
            SettingsScreenUiState.ServerStatus.Stopped
        },
        port = settingsState.serverPort,
        showNonDebuggableProperties = settingsState.showNonDebuggableProperties,
        sessions = connections.map {
            SettingsScreenUiState.SessionStatus(
                id = it.id,
                isActive = it.isActive,
            )
        },
        persistSessionData = settingsState.persistSessionData,
        databasePath = settingsState.databasePath,
        databaseStatus = when (val state = databaseState) {
            is BackInTimeDatabase.State.RunningInMemory -> SettingsScreenUiState.DatabaseStatus.InMemory
            is BackInTimeDatabase.State.RunningWithFile -> SettingsScreenUiState.DatabaseStatus.File(state.filePath)
            is BackInTimeDatabase.State.Stopped -> TODO()
        },
    )
}
