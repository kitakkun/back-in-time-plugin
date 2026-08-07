package com.kitakkun.backintime.feature.settings.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.component.SettingsHeadingItem
import com.kitakkun.backintime.feature.settings.component.SettingLabel
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

@Composable
fun InspectorSettingsSection(
    showNonDebuggableProperties: Boolean,
    onToggleShowNonDebuggableProperties: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SettingsHeadingItem(
            title = "Inspector",
            icon = Icons.Default.DataObject,
        )
        SettingsItemRow(
            label = { SettingLabel(text = "Show non-debuggable properties") },
            settingComponent = {
                Switch(
                    checked = showNonDebuggableProperties,
                    onCheckedChange = onToggleShowNonDebuggableProperties,
                )
            }
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
