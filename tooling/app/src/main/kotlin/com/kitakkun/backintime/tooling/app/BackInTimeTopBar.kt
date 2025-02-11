package com.kitakkun.backintime.tooling.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.model.Tab
import org.jetbrains.jewel.ui.component.SelectableIconActionButton
import org.jetbrains.jewel.ui.icons.AllIconsKeys

@Composable
fun BackInTimeTopBar(
    currentTab: Tab,
    onClickSettings: () -> Unit,
    onClickInstances: () -> Unit,
    onClickLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        SelectableIconActionButton(
            selected = currentTab == Tab.Inspector,
            onClick = onClickInstances,
            key = AllIconsKeys.Toolwindows.ToolWindowHierarchy,
            contentDescription = null,
        )
        SelectableIconActionButton(
            selected = currentTab == Tab.Log,
            onClick = onClickLog,
            key = AllIconsKeys.Nodes.DataSchema,
            contentDescription = null,
        )
        Spacer(Modifier.weight(1f))
        SelectableIconActionButton(
            selected = currentTab == Tab.Settings,
            onClick = onClickSettings,
            key = AllIconsKeys.General.Settings,
            contentDescription = null,
        )
    }
}

@Preview
@Composable
private fun BackInTimeTopBarPreview() {
    PreviewContainer {
        BackInTimeTopBar(
            currentTab = Tab.Inspector,
            onClickInstances = {},
            onClickLog = {},
            onClickSettings = {},
        )
    }
}
