package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.filterpopup.time

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun TimeFilterPopup(
    onDismissRequest: () -> Unit,
) {
    var startTimeText by remember { mutableStateOf("") }
    var endTimeText by remember { mutableStateOf("") }

    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        TimeFilterPopupView(
            startTimeText = startTimeText,
            endTimeText = endTimeText,
            onStartTimeTextUpdate = { startTimeText = it },
            onEndTimeTextUpdate = { endTimeText = it },
            onClickClose = onDismissRequest,
        )
    }
}
