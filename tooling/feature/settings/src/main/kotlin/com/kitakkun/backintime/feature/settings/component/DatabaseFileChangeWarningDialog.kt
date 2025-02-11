package com.kitakkun.backintime.feature.settings.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.ActionButton
import org.jetbrains.jewel.ui.component.Checkbox
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun DatabaseFileChangeWarningDialog(
    databaseFilePath: String,
    onClickOk: (migrate: Boolean) -> Unit,
    onClickCancel: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    var migrateDataToInMemoryDatabase by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .background(
                    color = JewelTheme.globalColors.panelBackground,
                    shape = RoundedCornerShape(32.dp),
                )
                .padding(32.dp)
        ) {
            Text(text = "The following file is being in use to handle debugger events:")
            Text(
                text = databaseFilePath,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .border(
                        width = 1.dp,
                        color = JewelTheme.globalColors.outlines.focusedWarning,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(8.dp),
            )
            Text(text = "Are you sure to switching to In-Memory database?")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Checkbox(
                    checked = migrateDataToInMemoryDatabase,
                    onCheckedChange = { migrateDataToInMemoryDatabase = it },
                )
                Text(text = "Migrate all events data to In-Memory database.(This will not delete current database file)")
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.End),
            ) {
                ActionButton(onClickCancel) {
                    Text(text = "Cancel")
                }
                DefaultButton(onClick = { onClickOk(migrateDataToInMemoryDatabase) }) {
                    Text(text = "OK")
                }
            }
        }
    }
}

@Preview
@Composable
private fun DatabaseFileChangeWarningDialogPreview() {
    PreviewContainer {
        DatabaseFileChangeWarningDialog(
            databaseFilePath = "/path/to/backintime-database.db",
            onDismissRequest = {},
            onClickOk = {},
            onClickCancel = {},
        )
    }
}
