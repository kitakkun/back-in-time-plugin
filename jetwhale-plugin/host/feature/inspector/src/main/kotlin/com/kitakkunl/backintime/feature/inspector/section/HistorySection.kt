package com.kitakkunl.backintime.feature.inspector.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.components.EventItemUiState
import com.kitakkunl.backintime.feature.inspector.components.EventSequenceView
import com.kitakkunl.backintime.feature.inspector.components.SelectedEventDetailView
import org.jetbrains.compose.splitpane.VerticalSplitPane

data class HistorySectionUiState(
    val events: List<EventItemUiState>,
    val selectedEventData: EventItemUiState?,
)

@Composable
fun HistorySection(
    uiState: HistorySectionUiState?,
    onClickEvent: (event: EventItemUiState) -> Unit,
    onPerformBackInTime: (event: EventItemUiState) -> Unit,
) {
    VerticalSplitPane {
        first(minSize = 200.dp) {
            uiState?.let {
                EventSequenceView(
                    items = it.events,
                    onClickEvent = onClickEvent,
                )
            } ?: Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "No instance is selected. No histories to show.")
            }
        }
        second(minSize = 50.dp) {
            uiState?.selectedEventData?.let {
                SelectedEventDetailView(
                    selectedEvent = it,
                    onPerformBackInTime = { onPerformBackInTime(it) }
                )
            }
        }
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
            onClickEvent = {},
            onPerformBackInTime = {},
        )
    }
}
