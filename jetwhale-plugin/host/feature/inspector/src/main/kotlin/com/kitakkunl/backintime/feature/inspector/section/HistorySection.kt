package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwEmptyState
import com.kitakkun.jetwhale.host.ui.JwSplitPane
import com.kitakkun.jetwhale.host.ui.rememberJwSplitPaneState
import com.kitakkunl.backintime.feature.inspector.components.EventItemUiState
import com.kitakkunl.backintime.feature.inspector.components.EventSequenceView
import com.kitakkunl.backintime.feature.inspector.components.SelectedEventDetailView
import kotlinx.coroutines.flow.distinctUntilChanged

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
    val splitPaneState = rememberJwSplitPaneState(dividerPosition)

    LaunchedEffect(splitPaneState) {
        snapshotFlow { splitPaneState.fraction }
            .distinctUntilChanged()
            .collect(onUpdateDividerPosition)
    }

    JwSplitPane(
        orientation = Orientation.Vertical,
        state = splitPaneState,
        firstMinSize = 160.dp,
        secondMinSize = 120.dp,
        first = {
            if (uiState == null) {
                JwEmptyState(title = "Select an instance to see what happened to it.")
            } else {
                EventSequenceView(
                    items = uiState.events,
                    onClickEvent = onClickEvent,
                )
            }
        },
        second = {
            val selected = uiState?.selectedEventData
            if (selected == null) {
                JwEmptyState(title = "Select an event to see its details.")
            } else {
                SelectedEventDetailView(
                    selectedEvent = selected,
                    onPerformBackInTime = { onPerformBackInTime(selected) },
                )
            }
        },
    )
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
