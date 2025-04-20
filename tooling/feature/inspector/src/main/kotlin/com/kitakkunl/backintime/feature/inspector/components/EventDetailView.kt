package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.BalloonView
import com.kitakkun.backintime.tooling.core.ui.component.TrianglePosition
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkunl.backintime.feature.inspector.model.toFunctionSignature
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.component.Text

@Composable
fun EventDetailView(
    uiState: EventItemUiState,
) {
    BalloonView(
        position = TrianglePosition.Top,
        radius = 10.dp,
        triangleSizeDp = 10.dp,
        borderWidth = 4.dp,
        modifier = Modifier.widthIn(max = 200.dp),
    ) {
        when (uiState) {
            is EventItemUiState.MethodInvocation -> {
                MethodInvocationDetailView(uiState)
            }

            is EventItemUiState.Register -> {
                // NONE
            }

            is EventItemUiState.Unregister -> {
                // NONE
            }
        }
    }
}

@Composable
private fun MethodInvocationDetailView(
    uiState: EventItemUiState.MethodInvocation,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "${uiState.invokedMethodSignature.asString()}(...)",
        )
        CompositionLocalProvider(LocalContentColor provides JewelTheme.contentColor.copy(alpha = 0.7f)) {
            if (uiState.stateChanges.isEmpty()) {
                Text("No state changes.")
            } else {
                uiState.stateChanges.forEach {
                    Row {
                        Text(text = it.signature.asString())
                        Spacer(
                            Modifier
                                .widthIn(min = 20.dp)
                                .weight(1f)
                        )
                        Text(
                            text = it.stateUpdates.joinToString(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun MethodInvocationDetailViewPreview() {
    PreviewContainer {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            MethodInvocationDetailView(
                uiState = EventItemUiState.MethodInvocation(
                    invokedMethodSignature = "reload".toFunctionSignature(),
                    stateChanges = emptyList(),
                    expandedDetails = true,
                    id = "",
                    selected = false,
                    time = 0,
                )
            )
        }
    }
}
