package com.kitakkun.backintime.tooling.feature.log.component

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.backintime.tooling.model.ClassInfo
import com.kitakkun.backintime.tooling.model.EventEntity
import com.kitakkun.jetwhale.host.ui.JwColumnOverflow
import com.kitakkun.jetwhale.host.ui.JwColumnWidth
import com.kitakkun.jetwhale.host.ui.JwEmptyState
import com.kitakkun.jetwhale.host.ui.JwTable
import com.kitakkun.jetwhale.host.ui.JwTableCellText
import com.kitakkun.jetwhale.host.ui.JwTableColumn
import com.kitakkun.jetwhale.host.ui.JwTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LogTableView(
    selectedEventId: String?,
    events: List<EventEntity>,
    onSelectEvent: (EventEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val codeStyle = JwTheme.textStyles.code
    val timeColor = JwTheme.colors.textSecondary

    val columns = remember(codeStyle, timeColor) {
        listOf(
            JwTableColumn<EventEntity>(
                header = "TIME",
                width = JwColumnWidth.Fixed(TimeColumnWidth),
            ) { event ->
                JwTableCellText(text = event.time.asWallClockTime(), style = codeStyle, color = timeColor)
            },
            // One line per event, always: a payload's `toString()` runs to hundreds of characters,
            // and letting it wrap turns a scannable table into a wall of text with rows metres
            // apart. The full value is what the detail pane below is for.
            JwTableColumn.text(
                header = "EVENT",
                width = JwColumnWidth.Weight(1f),
                overflow = JwColumnOverflow.Ellipsis,
                style = codeStyle,
            ) { event -> event.toString() },
        )
    }

    Box(modifier.fillMaxSize()) {
        JwTable(
            items = events,
            columns = columns,
            key = { it.eventId },
            isSelected = { it.eventId == selectedEventId },
            onClick = onSelectEvent,
            state = listState,
            emptyContent = { JwEmptyState(title = "No events recorded yet.") },
        )
        VerticalScrollbar(
            rememberScrollbarAdapter(listState),
            Modifier.align(Alignment.CenterEnd),
        )
    }
}

/**
 * Epoch millis are unreadable in a column you scan; what matters here is ordering and the gap
 * between events, both of which a wall clock shows at a glance. The exact millisecond value is
 * still in the payload the detail pane renders.
 */
private fun Long.asWallClockTime(): String =
    TimeFormatter.format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))

private val TimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")

private val TimeColumnWidth = 100.dp

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
