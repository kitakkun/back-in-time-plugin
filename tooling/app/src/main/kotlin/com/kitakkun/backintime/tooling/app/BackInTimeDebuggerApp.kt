package com.kitakkun.backintime.tooling.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.feature.settings.SettingsScreen
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import com.kitakkun.backintime.tooling.core.shared.IDENavigator
import com.kitakkun.backintime.tooling.core.shared.PluginStateService
import com.kitakkun.backintime.tooling.core.ui.component.HorizontalDivider
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalIDENavigator
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.feature.log.LogScreen
import com.kitakkun.backintime.tooling.model.Tab
import com.kitakkunl.backintime.feature.inspector.InspectorScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun BackInTimeDebuggerApp() {
    val server = LocalServer.current
    val serverState by server.stateFlow.collectAsState()
    val settings = LocalSettings.current

    val pluginStateService = LocalPluginStateService.current
    val pluginState by pluginStateService.stateFlow.collectAsState()

    LaunchedEffect(Unit) {
        if (!serverState.serverIsRunning) {
            server.restartServer(settings.getState().serverPort)
        }
    }

    // automatically select the new session if no session is selected.
    LaunchedEffect(server, pluginState) {
        snapshotFlow { serverState.connections }
            .distinctUntilChanged()
            .filter { it.isNotEmpty() }
            .collect {
                if (pluginState.globalState.selectedSessionId == null) {
                    pluginStateService.updateSessionId(it.first().id)
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalDivider()
        BackInTimeTopBar(
            currentTab = pluginState.globalState.activeTab,
            onClickInstances = { pluginStateService.updateTab(Tab.Inspector) },
            onClickLog = { pluginStateService.updateTab(Tab.Log) },
            onClickSettings = { pluginStateService.updateTab(Tab.Settings) },
        )
        HorizontalDivider()
        when (pluginState.globalState.activeTab) {
            Tab.Inspector -> InspectorScreen()
            Tab.Log -> LogScreen()
            Tab.Settings -> SettingsScreen()
        }
    }
}

@Preview
@Composable
private fun BackInTimeDebuggerAppPreview() {
    PreviewContainer {
        CompositionLocalProvider(
            LocalSettings provides BackInTimeDebuggerSettings.Dummy,
            LocalServer provides BackInTimeDebuggerService.Dummy,
            LocalPluginStateService provides PluginStateService.Dummy,
            LocalIDENavigator provides IDENavigator.Noop,
        ) {
            BackInTimeDebuggerApp()
        }
    }
}
