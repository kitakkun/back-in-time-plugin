package com.kitakkun.backintime.tooling.idea.debug

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kitakkun.backintime.tooling.core.shared.IDENavigator
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun IDENavigatorDebugSection(ideNavigator: IDENavigator) {
    Column {
        Text("IDENavigator")
        Spacer(Modifier.height(8.dp))
        TextFieldDebugActionItem(
            actionLabel = "Go",
            placeholderText = "ex) com/example/A.B",
            onPerformAction = ideNavigator::navigateToClass,
        )
        TextFieldDebugActionItem(
            actionLabel = "Go",
            placeholderText = "ex) com/example/A.prop",
            onPerformAction = ideNavigator::navigateToMemberProperty,
        )
        TextFieldDebugActionItem(
            actionLabel = "Go",
            placeholderText = "ex) com/example/Receiver com/example/A.function(kotlin/Int):kotlin/Unit",
            onPerformAction = ideNavigator::navigateToMemberFunction,
        )
    }
}

@Composable
private fun TextFieldDebugActionItem(
    actionLabel: String,
    onPerformAction: (inputText: String) -> Unit,
    placeholderText: String? = null,
) {
    val textFieldState = rememberTextFieldState()
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            state = textFieldState,
            placeholder = { placeholderText?.let { Text(it) } },
            modifier = Modifier.weight(1f),
        )
        DefaultButton(
            onClick = { onPerformAction(textFieldState.text.toString()) },
        ) {
            Text(actionLabel)
        }
    }
}
