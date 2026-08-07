package com.kitakkun.backintime.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDatabase
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.logic.EventEffect
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.usecase.LocalDatabase

sealed interface SettingsScreenEvent {
    data class UpdateNonDebuggablePropertyVisibility(val visible: Boolean) : SettingsScreenEvent
    data class RestartDatabaseWithFile(val databaseFilePath: String, val migrate: Boolean) : SettingsScreenEvent
    data class RestartDatabaseInMemory(val migrate: Boolean) : SettingsScreenEvent
}

@Composable
fun settingsScreenPresenter(eventEmitter: EventEmitter<SettingsScreenEvent>): SettingsScreenUiState {
    val database = LocalDatabase.current
    val settings = LocalSettings.current

    val databaseState by database.stateFlow.collectAsState()
    val settingsState by settings.stateFlow.collectAsState()
    EventEffect(eventEmitter) { event ->
        when (event) {
            is SettingsScreenEvent.UpdateNonDebuggablePropertyVisibility -> {
                settings.update {
                    it.copy(showNonDebuggableProperties = event.visible)
                }
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
        showNonDebuggableProperties = settingsState.showNonDebuggableProperties,
        persistSessionData = settingsState.persistSessionData,
        databasePath = settingsState.databasePath,
        databaseStatus = when (val state = databaseState) {
            is BackInTimeDatabase.State.RunningInMemory -> SettingsScreenUiState.DatabaseStatus.InMemory
            is BackInTimeDatabase.State.RunningWithFile -> SettingsScreenUiState.DatabaseStatus.File(state.filePath)
            is BackInTimeDatabase.State.Stopped -> TODO()
        },
    )
}
