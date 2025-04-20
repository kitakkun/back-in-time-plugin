package com.kitakkun.backintime.tooling.idea

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.painter.Painter
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconPainterResolver
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.icon.newUiChecker
import org.jetbrains.jewel.ui.icons.AllIconsKeys
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider

class BackInTimeIconPainterResolverImpl : BackInTimeIconPainterResolver {
    @Composable
    override fun resolveIconPainter(iconKey: BackInTimeIconsKey): Painter {
        val key = remember(iconKey) {
            when (iconKey) {
                BackInTimeIconsKey.Settings -> AllIconsKeys.General.Settings
                BackInTimeIconsKey.ToolWindowHierarchy -> AllIconsKeys.Toolwindows.ToolWindowHierarchy
                BackInTimeIconsKey.DataSchema -> AllIconsKeys.Nodes.DataSchema
                BackInTimeIconsKey.ArrowDown -> AllIconsKeys.General.ArrowDown
                BackInTimeIconsKey.ArrowRight -> AllIconsKeys.General.ArrowRight
                BackInTimeIconsKey.WebSocket -> AllIconsKeys.Webreferences.WebSocket
                BackInTimeIconsKey.EditSource -> AllIconsKeys.Actions.EditSource
                BackInTimeIconsKey.UiForm -> AllIconsKeys.FileTypes.UiForm
            }
        }
        val isNewUi = JewelTheme.newUiChecker.isNewUi()
        val path = remember(key, isNewUi) { key.path(isNewUi) }
        val painterProvider = rememberResourcePainterProvider(path, key.iconClass)
        val painter by painterProvider.getPainter()
        return painter
    }
}