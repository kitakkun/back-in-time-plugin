package com.kitakkun.backintime.feature.settings.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kitakkun.backintime.tooling.core.ui.component.CommonConfirmationDialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.Text

@Composable
fun DatabaseRecreationConfirmDialog(
    onDismissRequest: () -> Unit,
    onClickApply: (migrate: Boolean) -> Unit,
) {
    var migrateCurrentData by remember { mutableStateOf(false) }

    CommonConfirmationDialog(
        onDismissRequest = onDismissRequest,
        onClickOk = { onClickApply(migrateCurrentData) },
        onClickCancel = onDismissRequest,
    ) {
        Text("Database file location has been changed. Do you want to restart database?")
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = migrateCurrentData,
                onCheckedChange = { migrateCurrentData = it },
            )
            Text(
                text = "Migrate current data to new database file.",
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { migrateCurrentData = !migrateCurrentData },
                )
            )
        }
    }
}

@Preview
@Composable
private fun DatabaseRecreationConfirmDialogPreview() {
    PreviewContainer {
        DatabaseRecreationConfirmDialog(
            onDismissRequest = {},
            onClickApply = {},
        )
    }
}