package com.kitakkun.backintime.feature.settings.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.kitakkun.backintime.feature.settings.rememberFileChooserResultLauncher
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import com.kitakkun.jetwhale.host.ui.JwButton
import com.kitakkun.jetwhale.host.ui.JwButtonStyle
import com.kitakkun.jetwhale.host.ui.JwCheckbox
import com.kitakkun.jetwhale.host.ui.JwDialog
import com.kitakkun.jetwhale.host.ui.JwFormField
import com.kitakkun.jetwhale.host.ui.JwIcon
import com.kitakkun.jetwhale.host.ui.JwIconButton
import com.kitakkun.jetwhale.host.ui.JwSpacing
import com.kitakkun.jetwhale.host.ui.JwTextField
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun RestartDatabaseWithFileConfirmationDialog(
    initialDatabasePath: String?,
    onDismissRequest: () -> Unit,
    onClickOk: (databasePath: String, migrate: Boolean) -> Unit,
) {
    var databasePath by remember { mutableStateOf(initialDatabasePath.orEmpty()) }
    val fileChooserResultLauncher = rememberFileChooserResultLauncher {
        it ?: return@rememberFileChooserResultLauncher
        databasePath = it.absolutePath
    }
    var migrateCurrentData by remember { mutableStateOf(false) }

    JwDialog(
        onDismissRequest = onDismissRequest,
        title = "Persist session data to a file",
        closeLabel = "Close",
        confirmButton = {
            JwButton(
                text = "Restart",
                onClick = { onClickOk(databasePath, migrateCurrentData) },
                style = JwButtonStyle.Primary,
                enabled = databasePath.isNotEmpty(),
            )
        },
        dismissButton = {
            JwButton(text = "Cancel", onClick = onDismissRequest)
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(JwSpacing.large)) {
            JwFormField(label = "Database file") {
                JwTextField(
                    value = databasePath,
                    onValueChange = { databasePath = it },
                    placeholder = "/path/to/backintime-database.db",
                    trailingIcon = {
                        JwIconButton(
                            onClick = {
                                fileChooserResultLauncher.launch {
                                    selectedFile = File("backintime-database.db")
                                    fileFilter = FileNameExtensionFilter("sqlite database file", "db", "sqlite", "sqlite3")
                                    isAcceptAllFileFilterUsed = false
                                }
                            },
                            tooltip = "Choose a file",
                        ) {
                            JwIcon(Icons.Default.FileOpen, contentDescription = null)
                        }
                    },
                )
            }
            JwCheckbox(
                checked = migrateCurrentData,
                onCheckedChange = { migrateCurrentData = it },
                label = "Copy the current events into the new database file",
            )
        }
    }
}

@Preview
@Composable
fun RestartDatabaseWithFileConfirmationDialogPreview() {
    PreviewContainer {
        RestartDatabaseWithFileConfirmationDialog(
            initialDatabasePath = null,
            onDismissRequest = {},
            onClickOk = { _, _ -> },
        )
    }
}
