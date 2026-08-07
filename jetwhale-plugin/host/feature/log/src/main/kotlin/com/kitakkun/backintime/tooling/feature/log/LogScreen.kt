package com.kitakkun.backintime.tooling.feature.log

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
import com.kitakkun.backintime.tooling.core.ui.component.EmptyState
import com.kitakkun.backintime.tooling.core.ui.component.JsonView
import com.kitakkun.backintime.tooling.core.ui.component.verticalSplitter
import com.kitakkun.backintime.tooling.core.ui.logic.EventEmitter
import com.kitakkun.backintime.tooling.core.ui.logic.rememberEventEmitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.feature.log.component.LogTableView
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.Json
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

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
    val verticalSplitLayoutState = rememberSplitPaneState(uiState.verticalSplitDividerPosition)

    LaunchedEffect(verticalSplitLayoutState) {
        snapshotFlow { verticalSplitLayoutState.positionPercentage }
            .distinctUntilChanged()
            .collect(onUpdateVerticalSplitDividerPosition)
    }

    VerticalSplitPane(
        splitPaneState = verticalSplitLayoutState,
        modifier = Modifier.fillMaxSize(),
    ) {
        first(minSize = 200.dp) {
            LogTableView(
                events = uiState.events,
                selectedEventId = uiState.selectedEventId,
                onSelectEvent = { onSelectEvent(it) }
            )
        }
        second(minSize = 200.dp) {
            val selected = uiState.selectedEvent
            if (selected == null) {
                EmptyState(text = "Select a row to see the full event.")
            } else {
                JsonView(
                    jsonString = prettyJson.encodeToString(selected),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                )
            }
        }
        verticalSplitter()
    }
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
