package com.kitakkun.backintime.feature.settings.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.SettingsScreenUiState
import com.kitakkun.backintime.feature.settings.component.SettingsHeadingItem
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.component.Switch
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.jewel.ui.component.IconActionButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun DataBaseSettingsSection(
    status: SettingsScreenUiState.DatabaseStatus,
    needsDatabaseRestart: Boolean,
    persistSessionData: Boolean,
    databasePath: String?,
    onClickPickFile: () -> Unit,
    onTogglePersistSessionData: (Boolean) -> Unit,
    onUpdateDatabasePath: (String) -> Unit,
    onClickRestart: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val databaseTextFieldState = rememberTextFieldState(databasePath ?: "")

    LaunchedEffect(databasePath) {
        databaseTextFieldState.setTextAndPlaceCursorAtEnd(databasePath ?: "")
    }

    LaunchedEffect(databaseTextFieldState) {
        snapshotFlow { databaseTextFieldState.text.toString() }
            .distinctUntilChanged()
            .collect(onUpdateDatabasePath)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingsHeadingItem(
            title = "Database",
            iconKey = AllIconsKeys.Nodes.DataSchema,
        )
        SettingsItemRow(
            label = { Text("Status:") },
            settingComponent = {
                Text(
                    text = when (status) {
                        is SettingsScreenUiState.DatabaseStatus.File -> "Stored at ${status.path}"
                        is SettingsScreenUiState.DatabaseStatus.InMemory -> "Serving in memory"
                    }
                )
            }
        )
        SettingsItemRow(
            label = { Text("Persist session data:") },
            settingComponent = {
                Switch(
                    checked = persistSessionData,
                    onCheckedChange = onTogglePersistSessionData,
                )
            }
        )
        SettingsItemRow(
            label = { Text("DB file location:") },
            settingComponent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (needsDatabaseRestart) {
                        Text(
                            text = "Apply",
                            modifier = Modifier.clickable(
                                enabled = persistSessionData,
                                onClick = onClickRestart,
                            )
                        )
                    }
                    TextField(
                        enabled = persistSessionData,
                        state = databaseTextFieldState,
                        trailingIcon = {
                            IconActionButton(
                                enabled = persistSessionData,
                                key = AllIconsKeys.FileTypes.UiForm,
                                contentDescription = null,
                                onClick = onClickPickFile,
                            )
                        }
                    )
                }
            },
            modifier = Modifier.alpha(if (persistSessionData) 1f else 0.7f)
        )
    }
}

@Preview
@Composable
private fun DataSettingsSectionPreview() {
    PreviewContainer {
        DataBaseSettingsSection(
            persistSessionData = true,
            databasePath = "/Users/user/Documents/backintime-database.db",
            onClickPickFile = {},
            onTogglePersistSessionData = {},
            onUpdateDatabasePath = {},
            onClickRestart = {},
            status = SettingsScreenUiState.DatabaseStatus.InMemory,
            needsDatabaseRestart = true,
        )
    }
}
