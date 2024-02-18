package com.github.kitakkun.backintime.evaluation.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration

data class TodoItemState(
    val uuid: String,
    val label: String,
    val done: Boolean,
)

@Composable
fun TodoItemView(
    item: TodoItemState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        Checkbox(checked = item.done, onCheckedChange = onCheckedChange)
        Text(text = item.label, style = LocalTextStyle.current.copy(textDecoration = if (item.done) TextDecoration.LineThrough else TextDecoration.None))
    }
}
