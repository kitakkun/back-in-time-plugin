package com.kitakkun.backintime.tooling.core.ui.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.ActionButton
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun CommonConfirmationDialog(
    onDismissRequest: () -> Unit,
    onClickOk: () -> Unit,
    onClickCancel: () -> Unit,
    modifier: Modifier = Modifier,
    contentSpacing: Dp = 16.dp,
    enableOkButton: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = modifier
                .background(
                    shape = RoundedCornerShape(32.dp),
                    color = JewelTheme.globalColors.panelBackground,
                )
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(contentSpacing),
        ) {
            content()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.End),
            ) {
                ActionButton(onClickCancel) {
                    Text("Cancel")
                }
                DefaultButton(
                    enabled = enableOkButton,
                    onClick = onClickOk,
                ) {
                    Text("OK")
                }
            }
        }
    }
}

@Preview
@Composable
private fun CommonConfirmationDialogPreview() {
    PreviewContainer {
        CommonConfirmationDialog(
            onDismissRequest = {},
            onClickOk = {},
            onClickCancel = {},
        ) {
            Text("Description")
        }
    }
}
