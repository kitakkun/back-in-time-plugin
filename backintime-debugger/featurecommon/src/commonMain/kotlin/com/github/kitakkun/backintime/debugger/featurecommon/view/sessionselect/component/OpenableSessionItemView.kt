package com.github.kitakkun.backintime.debugger.featurecommon.view.sessionselect.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter

data class OpenableSessionBindModel(
    val sessionId: String,
    val sessionLabel: String,
    val createdAt: Long,
    val active: Boolean,
    val selected: Boolean,
)

@Composable
fun OpenableSessionItemView(
    bindModel: OpenableSessionBindModel,
    onToggleSessionSelection: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formattedCreatedAt = remember(bindModel.createdAt) {
        val instant = Instant.fromEpochMilliseconds(bindModel.createdAt)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        formatter.format(localDateTime.toJavaLocalDateTime())
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(
            checked = bindModel.selected,
            onCheckedChange = { onToggleSessionSelection() },
        )
        Text(
            text = bindModel.sessionLabel,
            style = DebuggerTheme.typography.labelMedium,
        )
        Text(
            text = formattedCreatedAt,
            style = DebuggerTheme.typography.labelSmall,
        )
    }
}

@Preview
@Composable
fun OpenableSessionItemViewPreview() {
    OpenableSessionItemView(
        bindModel = OpenableSessionBindModel(
            sessionLabel = "Session 1",
            createdAt = 0L,
            sessionId = "",
            active = false,
            selected = false,
        ),
        onToggleSessionSelection = {},
    )
}
