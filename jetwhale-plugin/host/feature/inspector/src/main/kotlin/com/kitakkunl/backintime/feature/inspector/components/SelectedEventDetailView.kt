package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.ui.component.SectionLabel
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            SectionLabel(text = "EVENT")
            KeyValueRow(
                key = "eventId",
                value = selectedEvent.id,
            )
            KeyValueRow(
                key = "time",
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
            Button(
                onClick = { showConfirmationDialog = true },
            ) {
                Text(text = "Back-in-time to this point")
            }
            OutlinedButton(onClick = {}) {
                Text(text = "Edit and emit")
            }
        }
    }
}

@Composable
private fun MethodInvocationDetailsView(
    event: EventItemUiState.MethodInvocation,
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(text = "UPDATED VALUES")
        if (event.stateChanges.isEmpty()) {
            Text(
                text = "This call changed no debuggable state.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            event.stateChanges.forEach {
                KeyValueRow(
                    key = it.signature.propertyName,
                    value = it.stateUpdates.joinToString(", "),
                )
            }
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
