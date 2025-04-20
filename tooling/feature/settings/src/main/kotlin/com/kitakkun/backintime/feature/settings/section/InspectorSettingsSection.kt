package com.kitakkun.backintime.feature.settings.section

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.component.SettingsHeadingItem
import com.kitakkun.backintime.feature.settings.component.SettingsItemRow
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import com.kitakkun.backintime.tooling.core.ui.component.Switch
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.Text

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
            iconKey = BackInTimeIconsKey.ToolWindowHierarchy,
        )
        SettingsItemRow(
            label = { Text(text = "Show non-debuggable properties:") },
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
