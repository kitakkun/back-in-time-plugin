package com.kitakkun.backintime.feature.settings.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.feature.settings.rememberFileChooserResultLauncher
import com.kitakkun.backintime.tooling.core.ui.component.CommonConfirmationDialog
import com.kitakkun.backintime.tooling.core.ui.preview.PreviewContainer
import java.io.File
import javax.swing.filechooser.FileNameExtensionFilter

@Composable
fun RestartDatabaseWithFileConfirmationDialog(
    initialDatabasePath: String?,
    onDismissRequest: () -> Unit,
    onClickOk: (databasePath: String, migrate: Boolean) -> Unit,
) {
    val databaseTextFieldState = rememberTextFieldState(initialDatabasePath ?: "")
    val fileChooserResultLauncher = rememberFileChooserResultLauncher {
        it ?: return@rememberFileChooserResultLauncher
        databaseTextFieldState.setTextAndPlaceCursorAtEnd(it.absolutePath)
    }
    var migrateCurrentData by remember { mutableStateOf(false) }

    CommonConfirmationDialog(
        onDismissRequest = onDismissRequest,
        onClickCancel = onDismissRequest,
        onClickOk = { onClickOk(databaseTextFieldState.text.toString(), migrateCurrentData) },
        enableOkButton = databaseTextFieldState.text.isNotEmpty(),
    ) {
        Text(text = "DB file location:")
        TextField(
            state = databaseTextFieldState,
            trailingIcon = {
                IconButton(
                    onClick = {
                        fileChooserResultLauncher.launch {
                            selectedFile = File("backintime-database.db")
                            fileFilter = FileNameExtensionFilter("sqlite database file", "db", "sqlite", "sqlite3")
                            isAcceptAllFileFilterUsed = false
                        }
                    }
                ) {
                    Icon(Icons.Default.FileOpen, null)
                }
            },
            modifier = Modifier.widthIn(min = 300.dp),
        )
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
fun RestartDatabaseWithFileConfirmationDialogPreview() {
    PreviewContainer {
        RestartDatabaseWithFileConfirmationDialog(
            initialDatabasePath = null,
            onDismissRequest = {},
            onClickOk = { _, _ -> },
        )
    }
}