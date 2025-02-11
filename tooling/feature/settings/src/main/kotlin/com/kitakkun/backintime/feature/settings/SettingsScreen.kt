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
import com.kitakkun.backintime.feature.settings.component.DatabaseFileChangeWarningDialog
import com.kitakkun.backintime.feature.settings.component.DatabaseRecreationConfirmDialog
import com.kitakkun.backintime.feature.settings.component.ServerRestartConfirmationDialog
import com.kitakkun.backintime.feature.settings.section.DataBaseSettingsSection
import com.kitakkun.backintime.feature.settings.section.InspectorSettingsSection
import com.kitakkun.backintime.feature.settings.section.ServerSettingsSection
import com.kitakkun.backintime.tooling.core.ui.component.HorizontalDivider
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun SettingsScreen(
    eventEmitter: EventEmitter<SettingsScreenEvent> = rememberEventEmitter(),
    uiState: SettingsScreenUiState = settingsScreenPresenter(eventEmitter),
) {
    var showDatabaseRestartConfirmationDialog by remember { mutableStateOf(false) }
    var showServerRestartConfirmationDialog by remember { mutableStateOf(false) }
    var showDatabaseFileChangeWarningDialog by remember { mutableStateOf(false) }

    val fileChooserLauncher = rememberFileChooserResultLauncher {
        it ?: return@rememberFileChooserResultLauncher
        eventEmitter.tryEmit(SettingsScreenEvent.UpdateDBPath(it.absolutePath))
    }

    if (showDatabaseRestartConfirmationDialog) {
        DatabaseRecreationConfirmDialog(
            onDismissRequest = { showDatabaseRestartConfirmationDialog = false },
            onClickApply = { migrate ->
                eventEmitter.tryEmit(
                    SettingsScreenEvent.RestartDatabaseAsFile(
                        databaseFilePath = uiState.databasePath ?: return@DatabaseRecreationConfirmDialog,
                        migrate = migrate,
                    )
                )
                showDatabaseRestartConfirmationDialog = false
            },
        )
    }

    if (showServerRestartConfirmationDialog) {
        ServerRestartConfirmationDialog(
            onDismissRequest = { showServerRestartConfirmationDialog = false },
            onClickOk = {
                eventEmitter.tryEmit(SettingsScreenEvent.RestartServer)
                showServerRestartConfirmationDialog = false
            }
        )
    }

    if (showDatabaseFileChangeWarningDialog) {
        DatabaseFileChangeWarningDialog(
            databaseFilePath = uiState.databasePath!!,
            onDismissRequest = { showDatabaseFileChangeWarningDialog = false },
            onClickCancel = { showDatabaseFileChangeWarningDialog = false },
            onClickOk = {
                eventEmitter.tryEmit(SettingsScreenEvent.RestartDatabaseInMemory(it))
                eventEmitter.tryEmit(SettingsScreenEvent.UpdatePersistSessionData(false))
                showDatabaseFileChangeWarningDialog = false
            },
        )
    }

    SettingsScreen(
        uiState = uiState,
        onUpdatePortNumber = { eventEmitter.tryEmit(SettingsScreenEvent.UpdatePortNumber(it)) },
        onToggleShowNonDebuggableProperties = { eventEmitter.tryEmit(SettingsScreenEvent.UpdateNonDebuggablePropertyVisibility(it)) },
        onClickShowInspectorForSession = { eventEmitter.tryEmit(SettingsScreenEvent.ShowInspectorForSession(it)) },
        onClickShowLogForSession = { eventEmitter.tryEmit(SettingsScreenEvent.ShowLogForSession(it)) },
        onClickPickFile = {
            fileChooserLauncher.launch {
                selectedFile = File("backintime-database.db")
                fileFilter = FileNameExtensionFilter("sqlite database file", "db", "sqlite", "sqlite3")
                isAcceptAllFileFilterUsed = false
            }
        },
        onTogglePersistSessionData = { persistData ->
            if (uiState.databaseStatus is SettingsScreenUiState.DatabaseStatus.File) {
                // show warning
                showDatabaseFileChangeWarningDialog = true
            } else {
                eventEmitter.tryEmit(SettingsScreenEvent.UpdatePersistSessionData(persistData))
            }
        },
        onUpdateDatabasePath = { eventEmitter.tryEmit(SettingsScreenEvent.UpdateDBPath(it)) },
        onClickApplyDatabaseConfiguration = { showDatabaseRestartConfirmationDialog = true },
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
    val needsServerRestart: Boolean get() = serverStatus is ServerStatus.Running && port != serverStatus.port
    val needsDatabaseRestart: Boolean get() = (databaseStatus as? DatabaseStatus.File)?.path != databasePath

    sealed interface ServerStatus {
        data object Stopped : ServerStatus
        data class Running(
            val activeConnectionCount: Int,
            val port: Int,
        ) : ServerStatus
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
    onUpdateDatabasePath: (String) -> Unit,
    onClickApplyServerConfiguration: () -> Unit,
    onClickShowInspectorForSession: (sessionId: String) -> Unit,
    onClickShowLogForSession: (sessionId: String) -> Unit,
    onClickPickFile: () -> Unit,
    onClickApplyDatabaseConfiguration: () -> Unit,
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
            needsDatabaseRestart = uiState.needsDatabaseRestart,
            persistSessionData = uiState.persistSessionData,
            databasePath = uiState.databasePath,
            onTogglePersistSessionData = onTogglePersistSessionData,
            onUpdateDatabasePath = onUpdateDatabasePath,
            onClickPickFile = onClickPickFile,
            onClickRestart = onClickApplyDatabaseConfiguration,
        )
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PreviewContainer {
        SettingsScreen(
            uiState = SettingsScreenUiState(
                serverStatus = SettingsScreenUiState.ServerStatus.Running(
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
