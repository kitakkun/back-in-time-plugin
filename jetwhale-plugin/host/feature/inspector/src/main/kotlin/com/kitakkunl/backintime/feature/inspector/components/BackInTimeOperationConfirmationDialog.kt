package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwButton
import com.kitakkun.jetwhale.host.ui.JwButtonStyle
import com.kitakkun.jetwhale.host.ui.JwDialog
import com.kitakkun.jetwhale.host.ui.JwText

@Composable
fun BackInTimeOperationConfirmationDialog(
    onDismissRequest: () -> Unit,
    onClickCancel: () -> Unit,
    onClickOk: () -> Unit,
) {
    JwDialog(
        onDismissRequest = onDismissRequest,
        title = "Back-in-time",
        closeLabel = "Close",
        confirmButton = {
            JwButton(text = "Go back", onClick = onClickOk, style = JwButtonStyle.Primary)
        },
        dismissButton = {
            JwButton(text = "Cancel", onClick = onClickCancel)
        },
    ) {
        JwText(text = "The app's state will be restored to this point. Are you sure?")
    }
}

@Preview
@Composable
private fun BackInTimeOperationConfirmationDialogPreview() {
    PreviewContainer {
        BackInTimeOperationConfirmationDialog(
            onDismissRequest = {},
            onClickCancel = {},
            onClickOk = {},
        )
    }
}
