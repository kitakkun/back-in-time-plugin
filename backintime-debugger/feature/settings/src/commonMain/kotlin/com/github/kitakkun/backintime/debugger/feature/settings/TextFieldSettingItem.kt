package com.github.kitakkun.backintime.debugger.feature.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.github.kitakkun.backintime.debugger.ui.primitive.BackInTimeDebuggerTheme

@Composable
fun TextFieldSettingItem(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var editable by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label)
        OutlinedTextField(
            readOnly = !editable,
            value = value,
            onValueChange = onValueChange,
            trailingIcon = {
                IconButton(onClick = { editable = !editable }) {
                    if (editable) {
                        Icon(
                            imageVector = Icons.Filled.Done,
                            contentDescription = "Done",
                            tint = BackInTimeDebuggerTheme.colorScheme.primary,
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                        )
                    }
                }
            },
        )
    }
}

@Preview
@Composable
private fun TextFieldSettingItemPreview() {
    TextFieldSettingItem(
        label = "Label",
        value = "Value",
        onValueChange = {},
    )
}
