package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.filterpopup.kind

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.content.model.EventKind

@Composable
fun KindFilterPopup(
    selectedKinds: Set<EventKind>,
    onSelectedKindsUpdate: (Set<EventKind>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Popup(
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
    ) {
        KindFilterPopupView(
            selectedKinds = selectedKinds,
            onClickClose = onDismissRequest,
            onSelectedKindsUpdate = onSelectedKindsUpdate,
        )
    }
}
