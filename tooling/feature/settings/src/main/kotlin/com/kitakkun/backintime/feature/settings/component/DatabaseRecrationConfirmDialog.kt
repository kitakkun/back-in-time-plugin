package com.kitakkun.backintime.feature.settings.component

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
fun DatabaseRecreationConfirmDialog(
    onDismissRequest: () -> Unit,
    onClickApply: (migrate: Boolean) -> Unit,
) {
    var migrateCurrentData by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(8.dp),
                    color = JewelTheme.globalColors.panelBackground,
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.align(Alignment.End),
            ) {
                ActionButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                DefaultButton(onClick = { onClickApply(migrateCurrentData) }) {
                    Text("Apply")
                }
            }
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