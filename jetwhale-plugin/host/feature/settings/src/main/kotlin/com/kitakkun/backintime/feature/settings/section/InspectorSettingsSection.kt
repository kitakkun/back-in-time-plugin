package com.kitakkun.backintime.feature.settings.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.feature.settings.component.SettingLabel
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwSectionHeader
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwSwitch

@Composable
fun InspectorSettingsSection(
    showNonDebuggableProperties: Boolean,
    onToggleShowNonDebuggableProperties: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(JwSpacing.medium),
    ) {
        JwSectionHeader(title = "Inspector")
        SettingsItemRow(
            label = { SettingLabel(text = "Show non-debuggable properties") },
            settingComponent = {
                JwSwitch(
                    checked = showNonDebuggableProperties,
                    onCheckedChange = onToggleShowNonDebuggableProperties,
                    contentDescription = "Show non-debuggable properties",
                )
            },
        )
    }
}

@Preview
@Composable
private fun InspectorSettingsSectionPreview() {
    PreviewContainer {
        InspectorSettingsSection(
            showNonDebuggableProperties = true,
            onToggleShowNonDebuggableProperties = {},
        )
    }
}
