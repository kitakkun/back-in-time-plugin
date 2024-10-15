package io.github.kitakkun.backintime.debugger.featurecommon.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

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
