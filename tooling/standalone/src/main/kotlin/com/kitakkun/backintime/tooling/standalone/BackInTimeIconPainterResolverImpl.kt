package com.kitakkun.backintime.tooling.standalone

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconPainterResolver
import com.kitakkun.backintime.tooling.core.ui.component.BackInTimeIconsKey

class BackInTimeIconPainterResolverImpl : BackInTimeIconPainterResolver {
    @Composable
    override fun resolveIconPainter(iconKey: BackInTimeIconsKey): Painter {
        return rememberVectorPainter(
            when (iconKey) {
                BackInTimeIconsKey.Settings -> Icons.Default.Settings
                BackInTimeIconsKey.ToolWindowHierarchy -> Icons.Default.BugReport
                BackInTimeIconsKey.DataSchema -> Icons.Default.History
                BackInTimeIconsKey.ArrowDown -> Icons.Default.KeyboardArrowDown
                BackInTimeIconsKey.ArrowRight -> Icons.AutoMirrored.Filled.KeyboardArrowRight
                BackInTimeIconsKey.WebSocket -> Icons.Default.Cloud
                BackInTimeIconsKey.EditSource -> Icons.Default.Edit
                BackInTimeIconsKey.UiForm -> Icons.Default.Folder
            }
        )
    }
}
