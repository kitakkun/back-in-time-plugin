package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.tooling.core.ui.component.CommonConfirmationDialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer

@Composable
fun BackInTimeOperationConfirmationDialog(
    onDismissRequest: () -> Unit,
    onClickCancel: () -> Unit,
    onClickOk: () -> Unit,
) {
    CommonConfirmationDialog(
        onDismissRequest = onDismissRequest,
        onClickCancel = onClickCancel,
        onClickOk = onClickOk,
    ) {
        Text(text = "Are you sure to back-in-time to this point?")
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
