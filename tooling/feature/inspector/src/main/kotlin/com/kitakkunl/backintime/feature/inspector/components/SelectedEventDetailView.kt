package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
            onClickOk = onPerformBackInTime,
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
                is EventItemUiState.Register,
                is EventItemUiState.Unregister -> {
                    /* Show nothing */
                }
            }
        }
        DefaultButton(
            onClick = { showConfirmationDialog = true },
        ) {
            Text(text = "Back-in-time to this point")
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
                key = it.name,
                value = it.stateUpdates.joinToString(", "),
            )
        }
    }
}