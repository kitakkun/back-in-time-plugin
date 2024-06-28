package com.github.kitakkun.backintime.debugger.feature.instance.view.list

import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.kitakkun.backintime.debugger.feature.instance.view.InstanceSharedViewModel
import com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.PropertyInspectorPage
import com.github.kitakkun.backintime.debugger.featurecommon.view.sessiontab.SessionTabPage
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import java.awt.Cursor

@OptIn(ExperimentalSplitPaneApi::class, KoinExperimentalAPI::class)
@Composable
fun InstanceListPage(navController: NavController) {
    val sharedViewModel: InstanceSharedViewModel = koinNavViewModel()
    val propertyArgs by sharedViewModel.propertyArgFlow.collectAsState()

    SessionTabPage { sessionId ->
        HorizontalSplitPane {
            first(minSize = 750.dp) {
                SessionInstancePage(
                    sessionId = sessionId,
                    navController = navController,
                )
            }
            second {
                propertyArgs?.let {
                    PropertyInspectorPage(params = it)
                }
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
