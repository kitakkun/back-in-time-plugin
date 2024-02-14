package com.github.kitakkun.backintime.evaluation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddTodoDialog(
    onConfirm: (label: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var editingLabel by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
        ) {
            TextField(
                value = editingLabel,
                onValueChange = { editingLabel = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilledTonalButton(onClick = onDismiss) {
                    Text(text = "Cancel")
                }
                Button(onClick = { onConfirm(editingLabel) }) {
                    Text(text = "OK")
                }
            }
        }
    }
}
