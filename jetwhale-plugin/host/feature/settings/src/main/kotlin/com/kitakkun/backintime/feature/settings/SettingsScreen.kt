package com.kitakkun.backintime.feature.settings

import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.feature.settings.component.RestartDatabaseInMemoryConfirmationDialog
import com.kitakkun.backintime.feature.settings.component.RestartDatabaseWithFileConfirmationDialog
import com.kitakkun.backintime.feature.settings.section.DataBaseSettingsSection
import com.kitakkun.backintime.feature.settings.section.InspectorSettingsSection
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwHorizontalDivider
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwTheme

@Composable
fun SettingsScreen(
    eventEmitter: EventEmitter<SettingsScreenEvent> = rememberEventEmitter(),
    uiState: SettingsScreenUiState = settingsScreenPresenter(eventEmitter),
) {
    var showRestartDatabaseInMemoryConfirmationDialog by remember { mutableStateOf(false) }
    var showRestartDatabaseWithFileConfirmationDialog by remember { mutableStateOf(false) }

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
        onToggleShowNonDebuggableProperties = { eventEmitter.tryEmit(SettingsScreenEvent.UpdateNonDebuggablePropertyVisibility(it)) },
        onTogglePersistSessionData = { persist ->
            if (persist) {
                showRestartDatabaseWithFileConfirmationDialog = true
            } else {
                showRestartDatabaseInMemoryConfirmationDialog = true
            }
        },
    )
}

data class SettingsScreenUiState(
    val databaseStatus: DatabaseStatus,
    val showNonDebuggableProperties: Boolean,
    val persistSessionData: Boolean,
    val databasePath: String?,
) {
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
    onToggleShowNonDebuggableProperties: (visible: Boolean) -> Unit,
    onTogglePersistSessionData: (persist: Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            // The pane is a plain column of rows rather than a component that paints its own
            // ground, so it fills the ground itself -- the host does not paint behind the scene
            // an MCP capture renders.
            .background(JwTheme.colors.surface)
            .padding(JwSpacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(JwSpacing.extraLarge),
    ) {
        InspectorSettingsSection(
            showNonDebuggableProperties = uiState.showNonDebuggableProperties,
            onToggleShowNonDebuggableProperties = onToggleShowNonDebuggableProperties,
        )
        JwHorizontalDivider()
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
                showNonDebuggableProperties = true,
                persistSessionData = false,
                databasePath = null,
                databaseStatus = SettingsScreenUiState.DatabaseStatus.InMemory,
            )
        )
    }
}
