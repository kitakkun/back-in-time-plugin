package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.ActionButton
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun SelectedEventDetailView(
    selectedEvent: EventItemUiState,
    onPerformBackInTime: () -> Unit,
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }

    if (showConfirmationDialog) {
        BackInTimeOperationConfirmationDialog(
            onDismissRequest = { showConfirmationDialog = false },
            onClickCancel = { showConfirmationDialog = false },
            onClickOk = {
                onPerformBackInTime()
                showConfirmationDialog = false
            },
        )
    }

    Column {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            KeyValueRow(
                key = "eventId:",
                value = selectedEvent.id,
            )
            KeyValueRow(
                key = "time:",
                value = selectedEvent.time.toString(),
            )
            when (selectedEvent) {
                is EventItemUiState.MethodInvocation -> MethodInvocationDetailsView(selectedEvent)
                is EventItemUiState.Register -> RegisterDetailsView(selectedEvent)
                is EventItemUiState.Unregister -> {
                    /* Show nothing */
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            DefaultButton(
                onClick = { showConfirmationDialog = true },
            ) {
                Text(text = "Back-in-time to this point")
            }
            ActionButton(onClick = {}) {
                Text(text = "Edit and emit")
            }
        }
    }
}

@Composable
private fun MethodInvocationDetailsView(
    event: EventItemUiState.MethodInvocation,
) {
    Column {
        Text("Updated Values")
        event.stateChanges.forEach {
            KeyValueRow(
                key = it.signature.asString(),
                value = it.stateUpdates.joinToString(", "),
            )
        }
    }
}

@Composable
private fun RegisterDetailsView(event: EventItemUiState.Register) {
}

@Preview
@Composable
private fun SelectedEventDetailViewPreview() {
    PreviewContainer {
        SelectedEventDetailView(
            selectedEvent = EventItemUiState.Register(
                id = "",
                selected = false,
                expandedDetails = false,
                time = 0,
            ),
            onPerformBackInTime = {},
        )
    }
}
