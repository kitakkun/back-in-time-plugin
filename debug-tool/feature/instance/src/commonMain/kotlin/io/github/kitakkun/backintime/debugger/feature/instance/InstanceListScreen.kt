package io.github.kitakkun.backintime.debugger.feature.instance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import backintime.debug_tool.feature.instance.generated.resources.Res
import backintime.debug_tool.feature.instance.generated.resources.no_session_opened
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.InstanceUiState
import io.github.kitakkun.backintime.debugger.feature.instance.component.list.PropertyUiState
import io.github.kitakkun.backintime.debugger.feature.instance.section.InstanceList
import io.github.kitakkun.backintime.debugger.feature.instance.section.InstanceListUiState
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScaffold
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenEvent
import io.github.kitakkun.backintime.debugger.featurecommon.SessionTabScreenUiState
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.EventEmitter
import io.github.kitakkun.backintime.debugger.featurecommon.architecture.rememberEventEmitter
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi
import org.jetbrains.compose.splitpane.HorizontalSplitPane
import java.awt.Cursor

@Serializable
object InstanceListScreenRoute

fun NavGraphBuilder.instanceListScreen(
    sessionTabEventEmitter: EventEmitter<SessionTabScreenEvent>,
    sessionTabUiState: SessionTabScreenUiState,
    onClickHistory: (sessionId: String, instanceId: String) -> Unit,
    onClickSettings: () -> Unit,
) {
    composable<InstanceListScreenRoute> {
        InstanceListScreen(
            sessionTabEventEmitter = sessionTabEventEmitter,
            sessionTabUiState = sessionTabUiState,
            onClickHistory = onClickHistory,
            onClickSettings = onClickSettings,
        )
    }
}

fun NavController.navigateToInstanceListScreen() {
    navigate(InstanceListScreenRoute) {
        restoreState = true
    }
}

@Composable
fun InstanceListScreen(
    sessionTabEventEmitter: EventEmitter<SessionTabScreenEvent>,
    sessionTabUiState: SessionTabScreenUiState,
    onClickHistory: (sessionId: String, instanceId: String) -> Unit,
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
        InstanceListScreen(
            sessionId = sessionId,
            onClickHistory = { onClickHistory(sessionId, it.id) },
        )
    }
}

@Composable
fun InstanceListScreen(
    sessionId: String,
    onClickHistory: (InstanceUiState) -> Unit,
    eventEmitter: EventEmitter<InstanceListEvent> = rememberEventEmitter(),
    uiState: InstanceListScreenUiState = instanceListScreenPresenter(eventEmitter, sessionId),
) {
    InstanceListScreen(
        uiState = uiState,
        onClickProperty = { instance, property ->
            eventEmitter.tryEmit(InstanceListEvent.SelectProperty(instance.id, property.name))
        },
        onClickExpand = { instance ->
            eventEmitter.tryEmit(InstanceListEvent.TogglePropertyVisibility(instance.id))
        },
        onClickHistory = onClickHistory,
    )
}

sealed interface InstanceListScreenUiState {
    data object NoSessionOpened : InstanceListScreenUiState
    data class Opened(
        val sessionInstanceListUiState: InstanceListUiState,
        val propertyInspectorUiState: PropertyInspectorScreenUiState,
    ) : InstanceListScreenUiState
}

@OptIn(ExperimentalSplitPaneApi::class)
@Composable
fun InstanceListScreen(
    uiState: InstanceListScreenUiState,
    onClickProperty: (InstanceUiState, PropertyUiState) -> Unit,
    onClickHistory: (InstanceUiState) -> Unit,
    onClickExpand: (InstanceUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (uiState) {
        is InstanceListScreenUiState.NoSessionOpened -> {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .widthIn(max = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
            ) {
                Text(
                    text = stringResource(Res.string.no_session_opened),
                    textAlign = TextAlign.Center,
                )
            }
        }

        is InstanceListScreenUiState.Opened -> {
            HorizontalSplitPane(
                modifier = modifier,
            ) {
                first(minSize = 750.dp) {
                    InstanceList(
                        uiState = uiState.sessionInstanceListUiState,
                        onClickHistory = onClickHistory,
                        onClickProperty = onClickProperty,
                        onClickExpand = onClickExpand,
                    )
                }
                second {
                    PropertyInspectorScreen(
                        uiState = uiState.propertyInspectorUiState,
                        onToggleSortWithTime = {},
                        onToggleSortWithValue = {},
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
    }
}
