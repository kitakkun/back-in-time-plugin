package com.kitakkun.backintime.tooling.idea

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.intellij.ide.ui.LafManager
import com.intellij.ide.ui.LafManagerListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.kitakkun.backintime.tooling.app.BackInTimeDebuggerApp
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalIDENavigator
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.theme.LocalIsIDEInDarkTheme
import com.kitakkun.backintime.tooling.core.usecase.LocalDatabase
import com.kitakkun.backintime.tooling.idea.service.BackInTimeDebuggerServiceImpl
import com.kitakkun.backintime.tooling.idea.service.BackInTimeDebuggerSettingsImpl
import com.kitakkun.backintime.tooling.idea.service.IDENavigatorImpl
import com.kitakkun.backintime.tooling.idea.service.PluginStateServiceImpl
import org.jetbrains.jewel.bridge.addComposeTab

class BackInTimeToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.addComposeTab {
            CompositionLocalProvider(
                LocalIsIDEInDarkTheme provides isIDEInDarkTheme(),
                LocalIDENavigator provides project.service<IDENavigatorImpl>(),
                LocalSettings provides BackInTimeDebuggerSettingsImpl.getInstance(),
                LocalServer provides BackInTimeDebuggerServiceImpl.getInstance(),
                LocalPluginStateService provides PluginStateServiceImpl.getInstance(),
                LocalDatabase provides BackInTimeDatabaseImpl.instance,
            ) {
                BackInTimeDebuggerApp()
            }
        }
    }

    @Composable
    private fun isIDEInDarkTheme(): Boolean {
        var isDark by remember { mutableStateOf(LafManager.getInstance().currentUIThemeLookAndFeel.isDark) }

        DisposableEffect(Unit) {
            val connection = ApplicationManager.getApplication().messageBus.connect()

            connection.subscribe(
                LafManagerListener.TOPIC, LafManagerListener {
                    isDark = it.currentUIThemeLookAndFeel.isDark
                }
            )

            onDispose {
                connection.dispose()
            }
        }

        return isDark
    }
}
