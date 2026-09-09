package com.kitakkun.backintime.tooling.feature.log

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.JsonView
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.feature.log.component.LogTableView
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkun.jetwhale.host.ui.JwEmptyState
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwSplitPane
import com.kitakkun.jetwhale.host.ui.rememberJwSplitPaneState
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json

@Composable
fun LogScreen(
    eventEmitter: EventEmitter<LogScreenEvent> = rememberEventEmitter(),
    uiState: LogScreenUiState = logScreenPresenter(eventEmitter),
) {
    LogScreen(
        uiState = uiState,
        onSelectEvent = { eventEmitter.tryEmit(LogScreenEvent.SelectEvent(it.eventId)) },
        onUpdateVerticalSplitDividerPosition = { eventEmitter.tryEmit(LogScreenEvent.UpdateVerticalSplitDividerPosition(it)) }
    )
}

data class LogScreenUiState(
    val selectedEventId: String?,
    val events: List<EventEntity>,
    val verticalSplitDividerPosition: Float,
) {
    val selectedEvent: EventEntity? get() = events.firstOrNull { it.eventId == selectedEventId }
}

@Composable
fun LogScreen(
    uiState: LogScreenUiState,
    onSelectEvent: (EventEntity) -> Unit,
    onUpdateVerticalSplitDividerPosition: (Float) -> Unit,
) {
    val verticalSplitLayoutState = rememberJwSplitPaneState(uiState.verticalSplitDividerPosition)

    LaunchedEffect(verticalSplitLayoutState) {
        snapshotFlow { verticalSplitLayoutState.fraction }
            .distinctUntilChanged()
            .collect(onUpdateVerticalSplitDividerPosition)
    }

    JwSplitPane(
        orientation = Orientation.Vertical,
        state = verticalSplitLayoutState,
        modifier = Modifier.fillMaxSize(),
        firstMinSize = 200.dp,
        secondMinSize = 200.dp,
        first = {
            LogTableView(
                events = uiState.events,
                selectedEventId = uiState.selectedEventId,
                onSelectEvent = { onSelectEvent(it) },
            )
        },
        second = {
            val selected = uiState.selectedEvent
            if (selected == null) {
                JwEmptyState(title = "Select a row to see the full event.")
            } else {
                JsonView(
                    jsonString = prettyJson.encodeToString(selected),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(JwSpacing.large),
                )
            }
        },
    )
}

/** The detail pane is the one place the whole payload is meant to be read, so it is indented. */
private val prettyJson = Json { prettyPrint = true }

@Preview
@Composable
private fun LogScreenPreview() {
    PreviewContainer {
        LogScreen(
            uiState = LogScreenUiState(
                events = List(10) {
                    EventEntity.Instance.Register(
                        sessionId = "sessionId",
                        instanceId = it.toString(),
                        classInfo = ClassInfo(
                            classSignature = "com/example/A",
                            superClassSignature = "com/example/B",
                            properties = emptyList(),
                        ),
                        time = 0,
                    )
                },
                verticalSplitDividerPosition = 0.5f,
                selectedEventId = null,
            ),
            onSelectEvent = {},
            onUpdateVerticalSplitDividerPosition = {},
        )
    }
}
