package com.kitakkun.backintime.feature.settings.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.SettingsScreenUiState
import com.kitakkun.backintime.feature.settings.component.SettingsHeadingItem
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import com.kitakkun.backintime.tooling.core.ui.component.Switch
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.Text

@Composable
fun DataBaseSettingsSection(
    status: SettingsScreenUiState.DatabaseStatus,
    persistSessionData: Boolean,
    onTogglePersistSessionData: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingsHeadingItem(
            title = "Database",
            iconKey = BackInTimeIconsKey.DataSchema,
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
    }
}

@Preview
@Composable
private fun DataSettingsSectionPreview() {
    PreviewContainer {
        DataBaseSettingsSection(
            persistSessionData = true,
            onTogglePersistSessionData = {},
            status = SettingsScreenUiState.DatabaseStatus.InMemory,
        )
    }
}
