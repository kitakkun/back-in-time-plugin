package com.kitakkun.backintime.tooling.standalone

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.kitakkun.backintime.tooling.app.BackInTimeDebuggerApp
import com.kitakkun.backintime.tooling.core.database.BackInTimeDatabaseImpl
import com.kitakkun.backintime.tooling.core.ui.component.LocalIconPainterResolver
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalIDENavigator
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalPluginStateService
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalServer
import com.kitakkun.backintime.tooling.core.ui.compositionlocal.LocalSettings
import com.kitakkun.backintime.tooling.core.ui.theme.LocalIsIDEInDarkTheme
import com.kitakkun.backintime.tooling.core.usecase.LocalDatabase
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.component.styling.DropdownIcons
import org.jetbrains.jewel.ui.component.styling.DropdownStyle
import org.jetbrains.jewel.ui.component.styling.LocalDefaultDropdownStyle
import org.jetbrains.jewel.ui.icon.IconKey

@OptIn(ExperimentalMaterialApi::class)
fun main() = application {
    Window(
        title = "Back-in-Time Debugger",
        onCloseRequest = ::exitApplication,
    ) {
        IntUiTheme {
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false,
                LocalDefaultDropdownStyle provides DropdownStyle(
                    colors = LocalDefaultDropdownStyle.current.colors,
                    metrics = LocalDefaultDropdownStyle.current.metrics,
                    icons = DropdownIcons(
                        object : IconKey {
                            override fun path(isNewUi: Boolean): String {
                                return "/arrow_drop_down.svg"
                            }

                            override val iconClass: Class<*> = javaClass
                        }
                    ),
                    menuStyle = LocalDefaultDropdownStyle.current.menuStyle,
                ),
                LocalIconPainterResolver provides BackInTimeIconPainterResolverImpl(),
                LocalIsIDEInDarkTheme provides true,
                LocalIDENavigator provides StandaloneIDENavigator(),
                LocalSettings provides StandaloneDebuggerSettings(),
                LocalServer provides StandaloneDebuggerService(),
                LocalPluginStateService provides StandalonePluginStateService(),
                LocalDatabase provides BackInTimeDatabaseImpl.instance,
            ) {
                BackInTimeDebuggerApp()
            }
        }
    }
} 