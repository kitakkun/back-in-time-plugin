package com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.InternalComposeApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.unit.dp

@Composable
fun SessionTabRow(
    selectedTabIndex: Int,
    openedTabItems: List<SessionTabItemBindModel>,
    onSelectSession: (SessionTabItemBindModel) -> Unit,
    onClickCloseSession: (SessionTabItemBindModel) -> Unit,
    onClickAddSession: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        edgePadding = 0.dp,
        modifier = modifier,
    ) {
        openedTabItems.forEachIndexed { index, bindModel ->
            SessionTabItemView(
                bindModel = bindModel,
                selected = index == selectedTabIndex,
                onSelectSession = onSelectSession,
                onClickCloseSession = onClickCloseSession,
            )
        }
        Tab(
            selected = false,
            text = { Icon(Icons.Default.Add, null) },
            onClick = { onClickAddSession() },
        )
    }
}

@OptIn(InternalComposeApi::class)
@Preview
@Composable
private fun SessionTabRowPreview() {
    CompositionLocalProvider(LocalSystemTheme provides SystemTheme.Light) {
        SessionTabRow(
            selectedTabIndex = 0,
            openedTabItems = listOf(
                SessionTabItemBindModel(sessionId = "session1", label = "Session 1", hasActiveConnection = true, selected = true),
                SessionTabItemBindModel(sessionId = "session2", label = "Session 2", hasActiveConnection = false, selected = false),
                SessionTabItemBindModel(sessionId = "session3", label = "Session 3", hasActiveConnection = false, selected = false),
            ),
            onSelectSession = {},
            onClickCloseSession = {},
            onClickAddSession = {},
        )
    }
}
