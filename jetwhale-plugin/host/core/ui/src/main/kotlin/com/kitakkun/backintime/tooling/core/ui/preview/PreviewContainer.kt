package com.kitakkun.backintime.tooling.core.ui.preview

import androidx.compose.runtime.Composable

@Composable
fun PreviewContainer(
    content: @Composable () -> Unit,
) {
    // After migrating from Jewel to native Compose Desktop,
    // Wrapper became unnecessary, so just call the content.
    content()
}
