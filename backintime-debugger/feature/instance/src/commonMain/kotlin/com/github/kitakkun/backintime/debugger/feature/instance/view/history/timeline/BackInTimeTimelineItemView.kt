package com.github.kitakkun.backintime.debugger.feature.instance.view.history.timeline

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class BackInTimeTimelineItemBindModel(
    override val id: String,
    override val timeMillis: Long,
    override val selected: Boolean,
    val rollbackDestinationId: String,
) : TimelineItemBindModel()

@Composable
fun BackInTimeTimelineItemView(
    bindModel: BackInTimeTimelineItemBindModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonColors(
            containerColor = DebuggerTheme.colorScheme.secondaryContainer,
            contentColor = DebuggerTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = DebuggerTheme.colorScheme.surfaceDim,
            disabledContentColor = DebuggerTheme.colorScheme.surfaceDim,
        ),
        modifier = modifier
            .size(60.dp)
            .then(
                if (bindModel.selected) {
                    Modifier.border(
                        width = 4.dp,
                        color = DebuggerTheme.colorScheme.primary,
                        shape = CircleShape,
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = DebuggerTheme.colorScheme.onSecondaryContainer,
                        shape = CircleShape,
                    )
                },
            ),
    ) {
        Icon(
            imageVector = Icons.Default.History,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.6f),
        )
    }
}

@Preview
@Composable
private fun BackInTimeTimelineItemViewPreview() {
    BackInTimeTimelineItemView(
        bindModel = BackInTimeTimelineItemBindModel(
            id = "1",
            timeMillis = 0,
            selected = false,
            rollbackDestinationId = "2",
        ),
        onClick = {},
    )
}
