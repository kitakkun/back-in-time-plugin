package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.animation.animateContentSize
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.Signature
import org.jetbrains.jewel.ui.component.Text

sealed interface EventItemUiState {
    val id: String
    val selected: Boolean
    val expandedDetails: Boolean
    val time: Long

    data class Register(
        override val id: String,
        override val selected: Boolean,
        override val expandedDetails: Boolean,
        override val time: Long,
    ) : EventItemUiState

    data class Unregister(
        override val id: String,
        override val selected: Boolean,
        override val expandedDetails: Boolean,
        override val time: Long,
    ) : EventItemUiState

    data class MethodInvocation(
        override val id: String,
        override val selected: Boolean,
        override val expandedDetails: Boolean,
        override val time: Long,
        val invokedMethodSignature: Signature.Function,
        val stateChanges: List<UpdatedProperty>,
    ) : EventItemUiState {
        data class UpdatedProperty(
            val signature: Signature.Property,
            val stateUpdates: List<String>,
        )
    }

    val color: Color
        get() = when (this) {
            is MethodInvocation -> if (stateChanges.isEmpty()) Color.Gray else Color.Red
            is Register -> Color.White
            is Unregister -> Color.Gray
        }

    val label: String
        get() = when (this) {
            is MethodInvocation -> "Method Call"
            is Register -> "Register"
            is Unregister -> "Unregister"
        }
}

val EventCircleIndicatorSize = 8.dp

@Composable
fun EventItemView(
    uiState: EventItemUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .animateContentSize()
            .clickable(onClick = onClick)
            .then(
                if (uiState.selected) {
                    Modifier.background(
                        color = Color.White.copy(alpha = 0.2f),
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = 2.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = uiState.color,
                    shape = CircleShape,
                )
                .size(EventCircleIndicatorSize),
        )
        Text(
            text = uiState.label,
            modifier = Modifier.wrapContentWidth(unbounded = true)
        )
        if (uiState.expandedDetails) {
            EventDetailView(uiState)
        }
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
                expandedDetails = false,
                time = 0,
            ),
            modifier = Modifier.height(100.dp),
            onClick = {},
        )
    }
}
