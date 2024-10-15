package io.github.kitakkun.backintime.debugger.feature.instance.component.history

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import backintime.debug_tool.feature.instance.generated.resources.Res
import backintime.debug_tool.feature.instance.generated.resources.instance_fill
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import org.jetbrains.compose.resources.painterResource

data class RegisterTimelineItemBindModel(
    override val id: String,
    override val timeMillis: Long,
    override val selected: Boolean,
) : TimelineItemBindModel()

@Composable
fun RegisterTimelineItemView(
    bindModel: RegisterTimelineItemBindModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        onClick = onClick,
        colors = IconButtonColors(
            containerColor = DebuggerTheme.colorScheme.primaryContainer,
            contentColor = DebuggerTheme.colorScheme.onPrimaryContainer,
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
                        color = DebuggerTheme.colorScheme.onPrimaryContainer,
                        shape = CircleShape,
                    )
                },
            ),
    ) {
        Icon(
            painter = painterResource(Res.drawable.instance_fill),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.6f),
        )
    }
}

@Preview
@Composable
private fun RegisterTimelineItemViewPreview() {
    RegisterTimelineItemView(
        bindModel = RegisterTimelineItemBindModel(
            id = "1",
            timeMillis = 0,
            selected = false,
        ),
        onClick = {},
    )
}
