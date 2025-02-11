package com.kitakkun.backintime.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.logic.EventEffect
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.usecase.LocalDatabase
import com.kitakkun.backintime.tooling.model.Tab

sealed interface SettingsScreenEvent {
    data class UpdatePortNumber(val portNumber: Int) : SettingsScreenEvent
    data class UpdatePersistSessionData(val persist: Boolean) : SettingsScreenEvent
    data class UpdateNonDebuggablePropertyVisibility(val visible: Boolean) : SettingsScreenEvent
    data class UpdateDBPath(val dbPath: String) : SettingsScreenEvent
    data class ShowInspectorForSession(val sessionId: String) : SettingsScreenEvent
    data class ShowLogForSession(val sessionId: String) : SettingsScreenEvent
    data object RestartServer : SettingsScreenEvent
    data class RestartDatabase(val migrate: Boolean) : SettingsScreenEvent
}

@Composable
fun settingsScreenPresenter(eventEmitter: EventEmitter<SettingsScreenEvent>): SettingsScreenUiState {
    val pluginStateService = LocalPluginStateService.current
    val database = LocalDatabase.current
    val server = LocalServer.current
    val settings = LocalSettings.current

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

            is SettingsScreenEvent.UpdateDBPath -> {
                settings.update {
                    it.copy(databasePath = event.dbPath)
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

            is SettingsScreenEvent.UpdatePersistSessionData -> {
                settings.loadState(settingsState.copy(persistSessionData = event.persist))
            }

            is SettingsScreenEvent.RestartDatabase -> {
//                database.restartDatabase(
//                    filePath = TODO(),
//                    migrate = event.migrate
//                )
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
        databaseStatus = SettingsScreenUiState.DatabaseStatus.InMemory,
    )
}
