package com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TimelineRow(
    timelineItems: List<TimelineItemBindModel>,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clipToBounds(),
        contentAlignment = Alignment.CenterStart,
    ) {
        LazyRow(
            state = listState,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            itemsIndexed(timelineItems) { index, history ->
                var hovered by remember { mutableStateOf(false) }
                if (history is BackInTimeTimelineItemBindModel) {
                    HorizontalDivider(
                        color = DebuggerTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .width(30.dp),
                    )
                }
                Box {
                    TimelineItemView(
                        bindModel = history,
                        modifier = Modifier
                            .onPointerEvent(eventType = PointerEventType.Enter) {
                                hovered = true
                            }
                            .onPointerEvent(eventType = PointerEventType.Exit) {
                                hovered = false
                            },
                    )
                    if (hovered) {
                        Popup {
                            Box(
                                modifier = Modifier
                                    .background(DebuggerTheme.colorScheme.onSurface)
                                    .size(200.dp),
                            ) {
                                Text(text = "More")
                            }
                        }
                    }
                }
                when {
                    index == 0 -> {
                        HorizontalDivider(
                            color = DebuggerTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(90.dp),
                        )
                    }

                    index != timelineItems.size - 1 -> {
                        HorizontalDivider(
                            color = DebuggerTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(30.dp),
                        )
                    }
                }
            }
        }

        if (listState.canScrollBackward) {
            timelineItems.firstOrNull()?.let {
                Row(
                    modifier = Modifier
                        .background(
                            color = DebuggerTheme.colorScheme.surface,
                            shape = HorizontalConcaveShape(startPercent = 50f, endPercent = 50f),
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TimelineItemView(it)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(90.dp)
                            .clip(HorizontalConcaveShape(startPercent = 0f, endPercent = 50f)),
                    ) {
                        HorizontalDivider(
                            color = DebuggerTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = null,
                            tint = DebuggerTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TimelineRowPreview() {
    TimelineRow(
        timelineItems = listOf(
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
    )
}
