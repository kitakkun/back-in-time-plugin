package com.github.kitakkun.backintime.debugger.feature.instance.view.list.propertyinspector.filterpopup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun ValueFilterPopupPage(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        ValueFilterPopupView(
            modifier = modifier,
        )
    }
}