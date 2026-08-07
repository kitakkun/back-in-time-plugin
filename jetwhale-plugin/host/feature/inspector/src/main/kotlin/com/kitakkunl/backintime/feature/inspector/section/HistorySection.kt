package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.EmptyState
import com.kitakkun.backintime.tooling.core.ui.component.verticalSplitter
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.components.EventItemUiState
import com.kitakkunl.backintime.feature.inspector.components.EventSequenceView
import com.kitakkunl.backintime.feature.inspector.components.SelectedEventDetailView
import kotlinx.coroutines.flow.distinctUntilChanged
import org.jetbrains.compose.splitpane.VerticalSplitPane
import org.jetbrains.compose.splitpane.rememberSplitPaneState

data class HistorySectionUiState(
    val events: List<EventItemUiState>,
    val selectedEventData: EventItemUiState?,
)

@Composable
fun HistorySection(
    uiState: HistorySectionUiState?,
    dividerPosition: Float,
    onUpdateDividerPosition: (Float) -> Unit,
    onClickEvent: (event: EventItemUiState) -> Unit,
    onPerformBackInTime: (event: EventItemUiState) -> Unit,
) {
    val splitPaneState = rememberSplitPaneState(dividerPosition)

    LaunchedEffect(splitPaneState) {
        snapshotFlow { splitPaneState.positionPercentage }
            .distinctUntilChanged()
            .collect(onUpdateDividerPosition)
    }

    VerticalSplitPane(splitPaneState = splitPaneState) {
        first(minSize = 160.dp) {
            if (uiState == null) {
                EmptyState(text = "Select an instance to see what happened to it.")
            } else {
                EventSequenceView(
                    items = uiState.events,
                    onClickEvent = onClickEvent,
                )
            }
        }
        second(minSize = 120.dp) {
            val selected = uiState?.selectedEventData
            if (selected == null) {
                EmptyState(text = "Select an event to see its details.")
            } else {
                SelectedEventDetailView(
                    selectedEvent = selected,
                    onPerformBackInTime = { onPerformBackInTime(selected) },
                )
            }
        }
        verticalSplitter()
    }
}

@Preview
@Composable
private fun HistorySectionPreview() {
    PreviewContainer {
        HistorySection(
            uiState = HistorySectionUiState(
                events = emptyList(),
                selectedEventData = null,
            ),
            dividerPosition = 0.35f,
            onUpdateDividerPosition = {},
            onClickEvent = {},
            onPerformBackInTime = {},
        )
    }
}
