package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.filterpopup.time

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.kitakkun.backintime.debugger.ui.theme.DebuggerTheme
import com.github.kitakkun.backintime.log.generated.resources.Res
import com.github.kitakkun.backintime.log.generated.resources.end_time
import com.github.kitakkun.backintime.log.generated.resources.start_time
import com.github.kitakkun.backintime.log.generated.resources.time_filter
import org.jetbrains.compose.resources.stringResource

@Composable
fun TimeFilterPopupView(
    startTimeText: String,
    endTimeText: String,
    onStartTimeTextUpdate: (String) -> Unit,
    onEndTimeTextUpdate: (String) -> Unit,
    onClickClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(300.dp)
            .background(
                color = DebuggerTheme.colorScheme.surfaceBright,
                shape = DebuggerTheme.shapes.small,
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(Res.string.time_filter),
                style = DebuggerTheme.typography.titleSmall,
                color = DebuggerTheme.colorScheme.onSurface,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = DebuggerTheme.colorScheme.onSurface,
                modifier = Modifier.clickable(onClick = onClickClose),
            )
        }
        OutlinedTextField(
            value = startTimeText,
            onValueChange = onStartTimeTextUpdate,
            label = { Text(stringResource(Res.string.start_time)) },
            placeholder = { Text("YYYY-MM-DD HH:MM:SS") },
        )
        OutlinedTextField(
            value = endTimeText,
            onValueChange = onEndTimeTextUpdate,
            label = { Text(stringResource(Res.string.end_time)) },
            placeholder = { Text("YYYY-MM-DD HH:MM:SS") },
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.End),
        ) {
            Text(
                text = "clear filter",
                color = DebuggerTheme.colorScheme.primary,
            )
            Button(onClick = {}) {
                Text(
                    text = "Apply",
                )
            }
        }
    }
}

@Preview
@Composable
private fun TimeFilterPopupViewPreview() {
    TimeFilterPopupView(
        startTimeText = "",
        endTimeText = "",
        onStartTimeTextUpdate = {},
        onEndTimeTextUpdate = {},
        onClickClose = {},
    )
}
