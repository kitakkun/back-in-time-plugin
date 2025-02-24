package com.kitakkun.backintime.tooling.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeSelectableIconActionButton
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.model.Tab

@Composable
fun BackInTimeTopBar(
    currentTab: Tab,
    onClickSettings: () -> Unit,
    onClickInstances: () -> Unit,
    onClickLog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        BackInTimeSelectableIconActionButton(
            selected = currentTab == Tab.Inspector,
            onClick = onClickInstances,
            iconKey = BackInTimeIconsKey.ToolWindowHierarchy,
        )
        BackInTimeSelectableIconActionButton(
            selected = currentTab == Tab.Log,
            onClick = onClickLog,
            iconKey = BackInTimeIconsKey.DataSchema,
        )
        Spacer(Modifier.weight(1f))
        BackInTimeSelectableIconActionButton(
            selected = currentTab == Tab.Settings,
            onClick = onClickSettings,
            iconKey = BackInTimeIconsKey.Settings,
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
