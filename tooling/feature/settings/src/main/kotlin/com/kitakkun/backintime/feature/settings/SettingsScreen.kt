package com.kitakkun.backintime.feature.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.component.RestartDatabaseInMemoryConfirmationDialog
import com.kitakkun.backintime.feature.settings.component.RestartDatabaseWithFileConfirmationDialog
import com.kitakkun.backintime.feature.settings.component.ServerRestartConfirmationDialog
import com.kitakkun.backintime.feature.settings.section.DataBaseSettingsSection
import com.kitakkun.backintime.feature.settings.section.InspectorSettingsSection
import com.kitakkun.backintime.feature.settings.section.ServerSettingsSection
import com.kitakkun.backintime.tooling.core.ui.component.HorizontalDivider
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

@Composable
fun SettingsScreen(
    eventEmitter: EventEmitter<SettingsScreenEvent> = rememberEventEmitter(),
    uiState: SettingsScreenUiState = settingsScreenPresenter(eventEmitter),
) {
    var showServerRestartConfirmationDialog by remember { mutableStateOf(false) }

    var showRestartDatabaseInMemoryConfirmationDialog by remember { mutableStateOf(false) }
    var showRestartDatabaseWithFileConfirmationDialog by remember { mutableStateOf(false) }

    if (showServerRestartConfirmationDialog) {
        ServerRestartConfirmationDialog(
            onDismissRequest = { showServerRestartConfirmationDialog = false },
            onClickOk = {
                eventEmitter.tryEmit(SettingsScreenEvent.RestartServer)
                showServerRestartConfirmationDialog = false
            }
        )
    }

    if (showRestartDatabaseInMemoryConfirmationDialog) {
        RestartDatabaseInMemoryConfirmationDialog(
            databaseFilePath = uiState.databasePath!!,
            onDismissRequest = { showRestartDatabaseInMemoryConfirmationDialog = false },
            onClickCancel = { showRestartDatabaseInMemoryConfirmationDialog = false },
            onClickOk = {
                eventEmitter.tryEmit(SettingsScreenEvent.RestartDatabaseInMemory(it))
                showRestartDatabaseInMemoryConfirmationDialog = false
            },
        )
    }

    if (showRestartDatabaseWithFileConfirmationDialog) {
        RestartDatabaseWithFileConfirmationDialog(
            initialDatabasePath = uiState.databasePath,
            onDismissRequest = { showRestartDatabaseWithFileConfirmationDialog = false },
            onClickOk = { databaseFilePath, migrate ->
                eventEmitter.tryEmit(SettingsScreenEvent.RestartDatabaseWithFile(databaseFilePath, migrate))
                showRestartDatabaseWithFileConfirmationDialog = false
            },
        )
    }

    SettingsScreen(
        uiState = uiState,
        onUpdatePortNumber = { eventEmitter.tryEmit(SettingsScreenEvent.UpdatePortNumber(it)) },
        onToggleShowNonDebuggableProperties = { eventEmitter.tryEmit(SettingsScreenEvent.UpdateNonDebuggablePropertyVisibility(it)) },
        onClickShowInspectorForSession = { eventEmitter.tryEmit(SettingsScreenEvent.ShowInspectorForSession(it)) },
        onClickShowLogForSession = { eventEmitter.tryEmit(SettingsScreenEvent.ShowLogForSession(it)) },
        onTogglePersistSessionData = { persist ->
            if (persist) {
                showRestartDatabaseWithFileConfirmationDialog = true
            } else {
                showRestartDatabaseInMemoryConfirmationDialog = true
            }
        },
        onClickApplyServerConfiguration = { showServerRestartConfirmationDialog = true },
    )
}

data class SettingsScreenUiState(
    val serverStatus: ServerStatus,
    val databaseStatus: DatabaseStatus,
    val sessions: List<SessionStatus>,
    val port: Int,
    val showNonDebuggableProperties: Boolean,
    val persistSessionData: Boolean,
    val databasePath: String?,
) {
    val needsServerRestart: Boolean
        get() =
            (serverStatus is ServerStatus.Started && port != serverStatus.port) ||
                serverStatus is ServerStatus.Stopped || serverStatus is ServerStatus.Error

    sealed interface ServerStatus {
        data object Starting : ServerStatus
        data class Started(
            val activeConnectionCount: Int,
            val port: Int,
        ) : ServerStatus

        data object Stopping : ServerStatus
        data object Stopped : ServerStatus
        data class Error(val message: String) : ServerStatus
    }

    data class SessionStatus(
        val id: String,
        val isActive: Boolean,
    )

    sealed interface DatabaseStatus {
        data object InMemory : DatabaseStatus

        data class File(
            val path: String,
        ) : DatabaseStatus
    }
}

@Composable
fun SettingsScreen(
    uiState: SettingsScreenUiState,
    onUpdatePortNumber: (Int) -> Unit,
    onToggleShowNonDebuggableProperties: (visible: Boolean) -> Unit,
    onTogglePersistSessionData: (persist: Boolean) -> Unit,
    onClickApplyServerConfiguration: () -> Unit,
    onClickShowInspectorForSession: (sessionId: String) -> Unit,
    onClickShowLogForSession: (sessionId: String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ServerSettingsSection(
            portNumber = uiState.port,
            status = uiState.serverStatus,
            sessions = uiState.sessions,
            needsServerRestart = uiState.needsServerRestart,
            onUpdatePortNumber = onUpdatePortNumber,
            onClickApply = onClickApplyServerConfiguration,
            onClickShowInspectorForSession = onClickShowInspectorForSession,
            onClickShowLogForSession = onClickShowLogForSession,
        )
        HorizontalDivider()
        InspectorSettingsSection(
            showNonDebuggableProperties = uiState.showNonDebuggableProperties,
            onToggleShowNonDebuggableProperties = onToggleShowNonDebuggableProperties,
        )
        HorizontalDivider()
        DataBaseSettingsSection(
            status = uiState.databaseStatus,
            persistSessionData = uiState.persistSessionData,
            onTogglePersistSessionData = onTogglePersistSessionData,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PreviewContainer {
        SettingsScreen(
            uiState = SettingsScreenUiState(
                serverStatus = SettingsScreenUiState.ServerStatus.Started(
                    port = 50023,
                    activeConnectionCount = 10,
                ),
                sessions = List(10) {
                    SettingsScreenUiState.SessionStatus(
                        id = "session$it",
                        isActive = it == 0,
                    )
                },
                port = 50023,
                showNonDebuggableProperties = true,
                persistSessionData = false,
                databasePath = null,
                databaseStatus = SettingsScreenUiState.DatabaseStatus.InMemory,
            )
        )
    }
}
