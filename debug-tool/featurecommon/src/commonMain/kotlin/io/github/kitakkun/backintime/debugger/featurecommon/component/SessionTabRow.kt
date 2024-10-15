package io.github.kitakkun.backintime.debugger.featurecommon.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

@Composable
fun SessionTabRow(
    sessions: List<SessionItem>,
    onClickTabItem: (SessionItem) -> Unit,
    onCloseTabItem: (SessionItem) -> Unit,
    onClickAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items(sessions) { session ->
            SessionTabItemView(
                session = session,
                onClick = { onClickTabItem(session) },
                onClickClose = { onCloseTabItem(session) },
            )
        }
        item {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                tint = DebuggerTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(
                        onClick = onClickAdd,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple(),
                    )
                    .padding(8.dp)
                    .size(24.dp)
            )
        }
    }
}


@Preview
@Composable
private fun SessionTabRowPreview() {
    SessionTabRow(
        sessions = listOf(
            SessionItem(id = "session1", label = "Session 1", hasActiveConnection = true, selected = true),
            SessionItem(id = "session2", label = "Session 2", hasActiveConnection = false, selected = false),
            SessionItem(id = "session3", label = "Session 3", hasActiveConnection = false, selected = false),
        ),
        onClickTabItem = {},
        onCloseTabItem = {},
        onClickAdd = {},
    )
}
