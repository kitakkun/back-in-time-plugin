package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkunl.backintime.feature.inspector.model.toFunctionSignature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature

@Composable
fun EventSequenceView(
    items: List<EventItemUiState>,
    onClickEvent: (event: EventItemUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()
    // Read outside drawBehind: a draw scope is not a composition and cannot resolve the theme.
    val connectorColor = JwTheme.colors.border

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyRow(
            state = lazyListState,
            // Items size to their content and hang from a common top edge, so every marker sits on
            // one line while an expanded balloon is free to be as tall as it needs. Stretching them
            // to the pane height instead is what used to cut the balloon off at the bottom.
            verticalAlignment = Alignment.Top,
            contentPadding = PaddingValues(horizontal = JwSpacing.medium, vertical = JwSpacing.large),
            modifier = Modifier.matchParentSize(),
        ) {
            itemsIndexed(
                items = items,
            ) { index, item ->
                EventItemView(
                    uiState = item,
                    onClick = { onClickEvent(item) },
                    modifier = Modifier
                        .drawBehind {
                            if (index != 0) {
                                this.drawLine(
                                    color = connectorColor,
                                    strokeWidth = ConnectorStrokeWidth.toPx(),
                                    start = Offset(0f, EventCircleIndicatorSize.toPx() / 2),
                                    end = Offset(size.width / 2, EventCircleIndicatorSize.toPx() / 2),
                                )
                            }
                            if (index != items.size - 1) {
                                this.drawLine(
                                    color = connectorColor,
                                    strokeWidth = ConnectorStrokeWidth.toPx(),
                                    start = Offset(size.width / 2, EventCircleIndicatorSize.toPx() / 2),
                                    end = Offset(size.width, EventCircleIndicatorSize.toPx() / 2),
                                )
                            }
                        }
                )
            }
        }
        HorizontalScrollbar(
            adapter = rememberScrollbarAdapter(lazyListState),
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}

private val ConnectorStrokeWidth = 2.dp

@Preview
@Composable
private fun EventSequenceViewPreview() {
    PreviewContainer {
        EventSequenceView(
            items = mutableListOf<EventItemUiState>().apply {
                add(
                    EventItemUiState.Register(
                        id = "-1",
                        selected = true,
                        time = 0,
                    )
                )
                addAll(
                    List(10) {
                        EventItemUiState.MethodInvocation(
                            id = it.toString(),
                            stateChanges = listOf(
                                EventItemUiState.MethodInvocation.UpdatedProperty(
                                    signature = "prop1".toPropertySignature(),
                                    stateUpdates = listOf("new Value")
                                )
                            ),
                            invokedMethodSignature = "updateValues".toFunctionSignature(),
                            selected = false,
                            time = 0,
                        )
                    }
                )
            },
            onClickEvent = {},
        )
    }
}
