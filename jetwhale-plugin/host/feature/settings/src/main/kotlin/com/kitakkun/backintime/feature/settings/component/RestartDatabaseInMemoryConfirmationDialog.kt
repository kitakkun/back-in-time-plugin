package com.kitakkun.backintime.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwButton
import com.kitakkun.jetwhale.host.ui.JwButtonStyle
import com.kitakkun.jetwhale.host.ui.JwCheckbox
import com.kitakkun.jetwhale.host.ui.JwCodeBlock
import com.kitakkun.jetwhale.host.ui.JwDialog
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwText

@Composable
fun RestartDatabaseInMemoryConfirmationDialog(
    databaseFilePath: String,
    onClickOk: (migrate: Boolean) -> Unit,
    onClickCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    var migrateDataToInMemoryDatabase by remember { mutableStateOf(false) }

    JwDialog(
        onDismissRequest = onDismissRequest,
        title = "Switch to an in-memory database",
        closeLabel = "Close",
        confirmButton = {
            JwButton(
                text = "Switch",
                onClick = { onClickOk(migrateDataToInMemoryDatabase) },
                style = JwButtonStyle.Primary,
            )
        },
        dismissButton = {
            JwButton(text = "Cancel", onClick = onClickCancel)
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(JwSpacing.large)) {
            JwText(text = "This file is currently in use to handle debugger events:")
            JwCodeBlock(text = databaseFilePath)
            JwCheckbox(
                checked = migrateDataToInMemoryDatabase,
                onCheckedChange = { migrateDataToInMemoryDatabase = it },
                label = "Copy the events into the in-memory database (the file is left untouched)",
            )
        }
    }
}

@Preview
@Composable
private fun RestartDatabaseInMemoryConfirmationDialogPreview() {
    PreviewContainer {
        RestartDatabaseInMemoryConfirmationDialog(
            databaseFilePath = "/path/to/backintime-database.db",
            onDismissRequest = {},
            onClickOk = {},
            onClickCancel = {},
        )
    }
}
