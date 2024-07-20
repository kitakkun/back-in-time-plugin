package io.github.kitakkun.backintime.debugger.feature.log

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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.kitakkun.backintime.debugger.feature.log.section.LogDetail
import io.github.kitakkun.backintime.debugger.feature.log.section.LogItemUiState
import io.github.kitakkun.backintime.debugger.feature.log.section.SessionLogTable
import io.github.kitakkun.backintime.debugger.feature.log.section.SessionLogTableUiState
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScaffold
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenEvent
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.component.EventKind
import io.github.kitakkun.backintime.debugger.featurecommon.sessionTabScreenPresenter
import kotlinx.serialization.Serializable
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import java.awt.Cursor

@Serializable
data object LogScreenRoute

fun NavGraphBuilder.logScreen(
    sessionTabEventEmitter: EventEmitter<SessionTabScreenEvent>,
    sessionTabUiState: SessionTabScreenUiState,
    onClickSettings: () -> Unit,
) {
    composable<LogScreenRoute> {
        LogScreen(
            sessionTabEventEmitter = sessionTabEventEmitter,
            sessionTabUiState = sessionTabUiState,
            onClickSettings = onClickSettings,
        )
    }
}

fun NavController.navigateToLogScreen() {
    navigate(LogScreenRoute) {
        restoreState = true
    }
}

@Composable
fun LogScreen(
    sessionTabEventEmitter: EventEmitter<SessionTabScreenEvent>,
    sessionTabUiState: SessionTabScreenUiState = sessionTabScreenPresenter(sessionTabEventEmitter),
    onClickSettings: () -> Unit,
) {
    SessionTabScaffold(
        eventEmitter = sessionTabEventEmitter,
        uiState = sessionTabUiState,
        tabActions = {
            IconButton(onClick = onClickSettings) {
                Icon(Icons.Default.Settings, null)
            }
        },
    ) { sessionId ->
        LogScreen(sessionId)
    }
}

@Composable
fun LogScreen(
    sessionId: String,
    eventEmitter: EventEmitter<LogScreenEvent> = rememberEventEmitter(),
    uiState: LogScreenUiState = logScreenPresenter(eventEmitter, sessionId),
) {
    LogScreen(
        uiState = uiState,
        onToggleSortWithTime = { eventEmitter.tryEmit(LogScreenEvent.ToggleSortWithTime) },
        onToggleSortWithKind = { eventEmitter.tryEmit(LogScreenEvent.ToggleSortWithKind) },
        onUpdateVisibleKinds = { eventEmitter.tryEmit(LogScreenEvent.VisibleKindsUpdated(it)) },
        onSelectLogItem = { eventEmitter.tryEmit(LogScreenEvent.SelectLogItem(it.id)) },
    )
}

data class LogScreenUiState(
    val tableUiState: SessionLogTableUiState,
)

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun LogScreen(
    uiState: LogScreenUiState,
    onToggleSortWithTime: () -> Unit,
    onToggleSortWithKind: () -> Unit,
    onUpdateVisibleKinds: (Set<EventKind>) -> Unit,
    onSelectLogItem: (LogItemUiState) -> Unit,
) {
    HorizontalSplitPane {
        first(750.dp) {
            SessionLogTable(
                uiState = uiState.tableUiState,
                onToggleSortWithTime = onToggleSortWithTime,
                onToggleSortWithKind = onToggleSortWithKind,
                onUpdateVisibleKinds = onUpdateVisibleKinds,
                onSelectLogItem = onSelectLogItem,
            )
        }
        second {
            LogDetail(
                selectedLogItem = uiState.tableUiState.selectedItem,
            )
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
