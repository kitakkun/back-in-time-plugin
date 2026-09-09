package com.kitakkun.backintime.feature.settings.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.feature.settings.SettingsScreenUiState
import com.kitakkun.backintime.feature.settings.component.SettingLabel
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwSectionHeader
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwSwitch
import com.kitakkun.jetwhale.host.ui.JwTag
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkun.jetwhale.host.ui.JwTone

@Composable
fun DataBaseSettingsSection(
    status: SettingsScreenUiState.DatabaseStatus,
    persistSessionData: Boolean,
    onTogglePersistSessionData: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(JwSpacing.medium),
    ) {
        JwSectionHeader(title = "Database")
        SettingsItemRow(
            label = { SettingLabel(text = "Status") },
            settingComponent = {
                when (status) {
                    // The path is read character by character to check it, so it gets the code
                    // style; the tag says which of the two modes is running at a glance.
                    is SettingsScreenUiState.DatabaseStatus.File -> JwText(
                        text = status.path,
                        style = JwTheme.textStyles.code,
                        color = JwTheme.colors.textSecondary,
                    )

                    is SettingsScreenUiState.DatabaseStatus.InMemory -> JwTag(
                        text = "In memory",
                        tone = JwTone.Info,
                    )
                }
            },
        )
        SettingsItemRow(
            label = { SettingLabel(text = "Persist session data") },
            settingComponent = {
                JwSwitch(
                    checked = persistSessionData,
                    onCheckedChange = onTogglePersistSessionData,
                    contentDescription = "Persist session data",
                )
            },
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
