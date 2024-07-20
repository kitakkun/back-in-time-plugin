package io.github.kitakkun.backintime.debugger.feature.instance.component.history

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material3.Badge
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class MethodInvocationTimelineItemBindModel(
    override val id: String,
    override val timeMillis: Long,
    override val selected: Boolean,
    val updatedPropertyCount: Int,
) : TimelineItemBindModel()

@Composable
fun MethodInvocationTimelineItemView(
    bindModel: MethodInvocationTimelineItemBindModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonColors(
            containerColor = DebuggerTheme.colorScheme.tertiaryContainer,
            contentColor = DebuggerTheme.colorScheme.onTertiaryContainer,
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
                        color = DebuggerTheme.colorScheme.onTertiaryContainer,
                        shape = CircleShape,
                    )
                },
            ),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            Icon(
                imageVector = Icons.Default.ChangeCircle,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.6f),
            )
            Badge(
                modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .align(Alignment.TopEnd),
            ) {
                Text(text = bindModel.updatedPropertyCount.toString())
            }
        }
    }
}

@Preview
@Composable
private fun MethodInvocationTimelineItemViewPreview() {
    MethodInvocationTimelineItemView(
        bindModel = MethodInvocationTimelineItemBindModel(
            id = "1",
            timeMillis = 0,
            selected = false,
            updatedPropertyCount = 3,
        ),
        onClick = {},
    )
}
