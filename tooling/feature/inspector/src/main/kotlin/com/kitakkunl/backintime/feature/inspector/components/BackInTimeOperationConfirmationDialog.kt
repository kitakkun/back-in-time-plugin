package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.kitakkun.backintime.tooling.core.ui.component.CommonConfirmationDialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.Text

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
