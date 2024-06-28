package com.github.kitakkun.backintime.debugger.feature.log.view.sessionlog.setting

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

@Composable
fun SessionLogSettingPopupPage() {
    Popup(
        properties = PopupProperties(focusable = true),
    ) {
        SessionLogSettingPopupView(
            sideBarLayoutOption = SideBarLayoutOption.VERTICAL,
            onUpdateLayoutOption = {},
        )
    }
}
