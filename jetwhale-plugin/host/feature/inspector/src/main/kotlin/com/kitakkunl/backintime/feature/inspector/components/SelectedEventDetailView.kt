package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwButton
import com.kitakkun.jetwhale.host.ui.JwButtonStyle
import com.kitakkun.jetwhale.host.ui.JwKeyValueRow
import com.kitakkun.jetwhale.host.ui.JwSectionHeader
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwText
import com.kitakkun.jetwhale.host.ui.JwTheme

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
            .background(JwTheme.colors.surface)
            .padding(JwSpacing.extraLarge),
        verticalArrangement = Arrangement.spacedBy(JwSpacing.large),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(JwSpacing.small),
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            JwSectionHeader(title = "Event")
            JwKeyValueRow(
                key = "eventId",
                value = selectedEvent.id,
                monospace = true,
                wrap = false,
            )
            JwKeyValueRow(
                key = "time",
                value = selectedEvent.time.toString(),
                monospace = true,
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
            horizontalArrangement = Arrangement.spacedBy(JwSpacing.medium),
        ) {
            JwButton(
                text = "Back-in-time to this point",
                onClick = { showConfirmationDialog = true },
                style = JwButtonStyle.Primary,
            )
            JwButton(text = "Edit and emit", onClick = {})
        }
    }
}

@Composable
private fun MethodInvocationDetailsView(
    event: EventItemUiState.MethodInvocation,
) {
    Column(verticalArrangement = Arrangement.spacedBy(JwSpacing.small)) {
        JwSectionHeader(title = "Updated values")
        if (event.stateChanges.isEmpty()) {
            JwText(
                text = "This call changed no debuggable state.",
                style = JwTheme.textStyles.bodySmall,
                color = JwTheme.colors.textSecondary,
            )
        } else {
            event.stateChanges.forEach {
                JwKeyValueRow(
                    key = it.signature.propertyName,
                    value = it.stateUpdates.joinToString(", "),
                    monospace = true,
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
                time = 0,
            ),
            onPerformBackInTime = {},
        )
    }
}
