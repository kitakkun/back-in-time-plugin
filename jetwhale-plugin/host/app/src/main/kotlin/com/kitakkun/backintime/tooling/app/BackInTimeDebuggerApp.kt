package com.kitakkun.backintime.tooling.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.feature.settings.SettingsScreen
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerService
import com.kitakkun.backintime.tooling.core.shared.BackInTimeDebuggerSettings
import com.kitakkun.backintime.tooling.core.shared.PluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSessionId
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.core.usecase.LocalDatabase
import com.kitakkun.backintime.tooling.feature.log.LogScreen
import com.kitakkun.backintime.tooling.model.BackInTimeTab
import com.kitakkun.jetwhale.host.ui.JwSurface
import com.kitakkun.jetwhale.host.ui.JwTab
import com.kitakkun.jetwhale.host.ui.JwTabRow
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkunl.backintime.feature.inspector.InspectorScreen

/**
 * The debugger UI, wired to one debug session.
 *
 * Everything session-scoped is passed in rather than looked up: the JetWhale host plugin owns the
 * session, the settings and the UI state (so they can be persisted into its own storage), and hands
 * them down here.
 */
@Composable
fun BackInTimeDebuggerAppWithCompositionLocals(
    sessionId: String,
    debuggerService: BackInTimeDebuggerService,
    settings: BackInTimeDebuggerSettings,
    pluginStateService: PluginStateService,
) {
    CompositionLocalProvider(
        LocalSessionId provides sessionId,
        LocalSettings provides settings,
        LocalServer provides debuggerService,
        LocalPluginStateService provides pluginStateService,
        LocalDatabase provides BackInTimeDatabaseImpl.instance,
    ) {
        BackInTimeDebuggerApp()
    }
}

@Composable
private fun BackInTimeDebuggerApp() {
    val pluginStateService = LocalPluginStateService.current
    val pluginState by pluginStateService.stateFlow.collectAsState()

    // The plugin is drawn straight onto the host's scene, which paints no background of its own;
    // without a surface the panes sit on whatever is behind them.
    JwSurface(
        color = JwTheme.colors.surface,
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // JwTabRow draws its own bottom hairline, so the pane below needs no divider of its own.
            JwTabRow {
                BackInTimeTab.entries.forEach { tab ->
                    JwTab(
                        selected = tab == pluginState.globalState.activeTab,
                        onClick = { pluginStateService.updateTab(tab) },
                        text = tab.name,
                    )
                }
            }
            when (pluginState.globalState.activeTab) {
                BackInTimeTab.Inspector -> InspectorScreen()
                BackInTimeTab.Log -> LogScreen()
                BackInTimeTab.Settings -> SettingsScreen()
            }
        }
    }
}

@Preview
@Composable
private fun BackInTimeDebuggerAppPreview() {
    PreviewContainer {
        CompositionLocalProvider(
            LocalSessionId provides "preview",
            LocalSettings provides BackInTimeDebuggerSettings.Dummy,
            LocalServer provides BackInTimeDebuggerService.Dummy,
            LocalPluginStateService provides PluginStateService.Dummy,
        ) {
            BackInTimeDebuggerApp()
        }
    }
}
