package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.graphics.Color
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.toFunctionSignature
import com.kitakkunl.backintime.feature.inspector.model.toPropertySignature

@Composable
fun EventSequenceView(
    items: List<EventItemUiState>,
    onClickEvent: (event: EventItemUiState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lazyListState = rememberLazyListState()

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        LazyRow(
            state = lazyListState,
            modifier = modifier.matchParentSize(),
        ) {
            itemsIndexed(
                items = items,
            ) { index, item ->
                EventItemView(
                    uiState = item,
                    onClick = { onClickEvent(item) },
                    modifier = Modifier
                        .fillParentMaxHeight()
                        .drawBehind {
                            if (index != 0) {
                                this.drawLine(
                                    color = Color.Red,
                                    start = Offset(0f, EventCircleIndicatorSize.toPx() / 2),
                                    end = Offset(size.width / 2, EventCircleIndicatorSize.toPx() / 2),
                                )
                            }
                            if (index != items.size - 1) {
                                this.drawLine(
                                    color = Color.Red,
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
                        expandedDetails = false,
                        time = 0,
                    )
                )
                addAll(
                    List(10) {
                        EventItemUiState.MethodInvocation(
                            id = it.toString(),
                            expandedDetails = it % 5 == 0,
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
