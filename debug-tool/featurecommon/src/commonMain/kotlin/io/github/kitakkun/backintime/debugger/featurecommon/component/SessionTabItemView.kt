package io.github.kitakkun.backintime.debugger.featurecommon.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme

data class SessionItem(
    val id: String,
    val label: String,
    val hasActiveConnection: Boolean,
    val selected: Boolean,
)

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SessionTabItemView(
    session: SessionItem,
    onClick: () -> Unit,
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dividerColor = DebuggerTheme.colorScheme.primary
    var hoveringOnCloseButton by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(),
            )
            .then(
                if (session.selected) {
                    Modifier.drawBehind {
                        drawLine(
                            color = dividerColor,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, size.height)
                        )
                    }
                } else {
                    Modifier
                }
            )
            .padding(
                start = 16.dp,
                end = 8.dp,
                top = 8.dp,
                bottom = 8.dp,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f),
        ) {
            Badge(
                containerColor = if (session.hasActiveConnection) {
                    DebuggerTheme.staticColors.activeGreen
                } else {
                    DebuggerTheme.staticColors.inactiveGrey
                },
                modifier = Modifier.size(12.dp),
            )
            Text(
                text = session.label,
                style = DebuggerTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = null,
            tint = DebuggerTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .clip(CircleShape)
                .clickable(
                    onClick = onClickClose,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                )
                .onPointerEvent(PointerEventType.Enter) {
                    hoveringOnCloseButton = true
                }
                .onPointerEvent(PointerEventType.Exit) {
                    hoveringOnCloseButton = false
                }
                .size(24.dp)
                .padding(4.dp)
                .alpha(
                    if (hoveringOnCloseButton) {
                        1f
                    } else {
                        0f
                    },
                )
        )
    }
}
