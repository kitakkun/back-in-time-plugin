package com.kitakkun.backintime.tooling.feature.log.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity

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
        LazyColumn(state = listState) {
            stickyHeader {
                HeaderRow()
            }
            items(
                items = events,
                key = { it.eventId },
            ) { event ->
                EventRow(
                    event = event,
                    selected = selectedEventId == event.eventId,
                    onClick = { onSelectEvent(event) },
                )
            }
        }
        VerticalScrollbar(
            rememberScrollbarAdapter(listState),
            Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun HeaderRow() {
    Column {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text(
                text = "TIME",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(TimeColumnWidth),
            )
            Text(
                text = "EVENT",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
private fun EventRow(
    event: EventEntity,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else Color.Transparent
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
    ) {
        Text(
            text = event.time.toString(),
            style = MonospacedRowStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.width(TimeColumnWidth),
        )
        // One line per event, always: a payload's `toString()` runs to hundreds of characters, and
        // letting it wrap turns a scannable table into a wall of text with rows metres apart. The
        // full value is what the detail pane below is for.
        Text(
            text = event.toString(),
            style = MonospacedRowStyle,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private val TimeColumnWidth = 110.dp
private val MonospacedRowStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 12.sp)

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
            modifier = Modifier.fillMaxSize(),
        )
    }
}
