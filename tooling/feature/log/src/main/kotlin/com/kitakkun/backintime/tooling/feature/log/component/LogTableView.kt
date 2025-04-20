package com.kitakkun.backintime.tooling.feature.log.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticalScrollbar

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LogTableView(
    selectedEventId: String?,
    events: List<EventEntity>,
    onSelectEvent: (EventEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()

    Box(modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = modifier.matchParentSize(),
        ) {
            stickyHeader {
                ItemRow(
                    timeText = "Time",
                    payloadText = "Payload",
                    modifier = Modifier.background(JewelTheme.globalColors.panelBackground)
                )
            }
            items(
                items = events,
                key = { it.eventId },
            ) {
                ItemRow(
                    timeText = it.time.toString(),
                    payloadText = it.toString(),
                    modifier = Modifier
                        .clickable(onClick = { onSelectEvent(it) })
                        .then(
                            if (selectedEventId == it.eventId) {
                                Modifier.background(Color.White.copy(alpha = 0.2f))
                            } else {
                                Modifier
                            }
                        )
                )
            }
        }
        VerticalScrollbar(listState, Modifier.align(Alignment.CenterEnd))
    }
}

@Composable
private fun ItemRow(
    timeText: String,
    payloadText: String,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Text(timeText, modifier = Modifier.width(100.dp))
        Text(payloadText)
    }
}

@Preview
@Composable
private fun LogTableViewPreview() {
    PreviewContainer {
        LogTableView(
            events = listOf(
                EventEntity.Instance.Register(
                    sessionId = "sessionId",
                    instanceId = "hogehoge",
                    classInfo = ClassInfo(classSignature = "com/example/A", superClassSignature = "com/example/B", properties = emptyList()),
                    time = 0,
                ),
                EventEntity.Instance.MethodInvocation(
                    sessionId = "sessionId",
                    instanceId = "hogehoge",
                    callId = "hoghoeg",
                    methodSignature = "com/example/A.hoge():kotlin/Unit",
                    time = 0,
                ),
            ),
            selectedEventId = null,
            onSelectEvent = {},
            modifier = Modifier.fillMaxSize()
        )
    }
}
