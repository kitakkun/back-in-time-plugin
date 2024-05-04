package com.github.kitakkun.backintime.debugger.feature.log.view.session_log

import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.kitakkun.backintime.debugger.feature.log.view.session_log.content.SessionLogContentPage
import com.github.kitakkun.backintime.debugger.feature.log.view.session_log.detail.SessionLogDetailPage
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.SessionTabPage
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import java.awt.Cursor

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun SessionLogPage(navController: NavController) {
    SessionTabPage(
        tabTrailingContent = {
            // TODO: Implement settings popup
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Default.Settings, null)
            }
        },
    ) { sessionId ->
        HorizontalSplitPane {
            first(minSize = 700.dp) {
                SessionLogContentPage(sessionId)
            }
            second(minSize = 300.dp) {
                SessionLogDetailPage()
            }
            splitter {
                visiblePart {
                    VerticalDivider()
                }
                handle {
                    VerticalDivider(
                        modifier = Modifier
                            .width(4.dp)
                            .markAsHandle()
                            .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR))),
                    )
                }
            }
        }
    }
}
