package com.kitakkun.backintime.feature.settings.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.kitakkun.backintime.tooling.core.ui.component.CommonConfirmationDialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ServerRestartConfirmationDialog(
    onClickOk: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    CommonConfirmationDialog(
        onDismissRequest = onDismissRequest,
        onClickOk = onClickOk,
        onClickCancel = onDismissRequest,
    ) {
        Text(text = "Server will restart.")
        Text(text = "All of the active sessions will be terminated.")
    }
}

@Preview
@Composable
private fun ServerRestartConfirmationDialogPreview() {
    PreviewContainer {
        ServerRestartConfirmationDialog(
            onClickOk = {},
            onDismissRequest = {},
        )
    }
}
