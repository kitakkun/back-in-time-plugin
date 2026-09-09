package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwShapes
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme
import com.kitakkun.jetwhale.host.ui.JwTone
import com.kitakkunl.backintime.feature.inspector.model.Signature

sealed interface EventItemUiState {
    val id: String
    val selected: Boolean
    val time: Long

    data class Register(
        override val id: String,
        override val selected: Boolean,
        override val time: Long,
    ) : EventItemUiState

    data class Unregister(
        override val id: String,
        override val selected: Boolean,
        override val time: Long,
    ) : EventItemUiState

    data class MethodInvocation(
        override val id: String,
        override val selected: Boolean,
        override val time: Long,
        val invokedMethodSignature: Signature.Function,
        val stateChanges: List<UpdatedProperty>,
    ) : EventItemUiState {
        data class UpdatedProperty(
            val signature: Signature.Property,
            val stateUpdates: List<String>,
        )
    }

    /**
     * A method call that changed nothing is a step you can skip past, so it is drawn muted; one
     * that did change state is what the timeline is for, and gets the accent.
     */
    @get:Composable
    val color: Color
        get() = when (this) {
            is MethodInvocation -> if (stateChanges.isEmpty()) JwTheme.colors.controlBorder else JwTheme.colors.accent
            // The two ends of an instance's life, in the tones the host uses for exactly that.
            is Register -> JwTone.Success.color
            is Unregister -> JwTheme.colors.textSecondary
        }

    val label: String
        get() = when (this) {
            is MethodInvocation -> "Method Call"
            is Register -> "Register"
            is Unregister -> "Unregister"
        }
}

val EventCircleIndicatorSize = 8.dp

/**
 * Width of one event on the timeline.
 *
 * Fixed rather than content-sized: the markers then sit at an even pitch, which is what makes the
 * row read as a timeline, and a label can never grow wide enough to overlap its neighbours.
 */
private val EventItemWidth = 108.dp

@Composable
fun EventItemView(
    uiState: EventItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(EventItemWidth)
            .clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(JwSpacing.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // The marker sits on the timeline that is drawn behind this item, so it stays outside the
        // selection highlight -- a highlight painted over the whole item covers the connecting line
        // and breaks the timeline it is meant to point at.
        Box(
            modifier = Modifier
                .background(
                    color = uiState.color,
                    shape = CircleShape,
                )
                .size(EventCircleIndicatorSize),
        )
        JwText(
            text = uiState.label,
            style = JwTheme.textStyles.label,
            color = if (uiState.selected) JwTheme.colors.onSelection else JwTheme.colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(
                    color = if (uiState.selected) JwTheme.colors.selection else Color.Transparent,
                    shape = JwShapes.medium,
                )
                .padding(horizontal = JwSpacing.medium, vertical = JwSpacing.tiny),
        )
    }
}

@Preview
@Composable
fun EventItemViewPreview() {
    PreviewContainer {
        EventItemView(
            uiState = EventItemUiState.Register(
                id = "",
                selected = false,
                time = 0,
            ),
            modifier = Modifier.height(100.dp),
            onClick = {},
        )
    }
}
