package com.kitakkunl.backintime.feature.inspector.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun BackInTimeOperationConfirmationDialog(
    onDismissRequest: () -> Unit,
    onClickCancel: () -> Unit,
    onClickOk: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    color = JewelTheme.globalColors.panelBackground,
                    shape = RoundedCornerShape(8.dp),
                )
                .padding(32.dp)
        ) {
            Text(text = "Are you sure to back-in-time to this point?")
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(onClick = onClickCancel) {
                    Text("Cancel")
                }
                DefaultButton(onClick = onClickOk) {
                    Text("OK")
                }
            }
        }
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
