package io.github.kitakkun.backintime.debugger.feature.instance.component.history

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kitakkun.backintime.debugger.feature.instance.HistoryScreenUiState
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

@Composable
fun HistoryLoadedView(
    bindModel: HistoryScreenUiState.Loaded,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Column(modifier) {
        Column(
            modifier = Modifier.padding(
                horizontal = 30.dp,
                vertical = 10.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            HorizontalScrollbar(
                adapter = rememberScrollbarAdapter(listState),
                style = LocalScrollbarStyle.current.copy(
                    unhoverColor = DebuggerTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    hoverColor = DebuggerTheme.colorScheme.onSurfaceVariant,
                ),
            )
            TimelineRow(
                timelineItems = bindModel.timelines,
                listState = listState,
            )
        }
        Spacer(Modifier.weight(1f))
        DiffView()
    }
}

@Composable
private fun DiffView() {
}

@Preview
@Composable
private fun HistoryLoadedViewPreview() {
    DebuggerTheme {
        HistoryLoadedView(
            bindModel = HistoryScreenUiState.Loaded(
                timelines = listOf(
                    RegisterTimelineItemBindModel(
                        timeMillis = System.currentTimeMillis(),
                        id = "",
                        selected = false,
                    ),
                    MethodInvocationTimelineItemBindModel(
                        timeMillis = System.currentTimeMillis(),
                        id = "",
                        selected = false,
                        updatedPropertyCount = 0,
                    ),
                    BackInTimeTimelineItemBindModel(
                        timeMillis = System.currentTimeMillis(),
                        id = "",
                        selected = false,
                        rollbackDestinationId = "",
                    ),
                    DisposeTimelineItemBindModel(
                        timeMillis = System.currentTimeMillis(),
                        id = "",
                        selected = false,
                    ),
                ),
            ),
        )
    }
}
