package com.kitakkun.backintime.tooling.core.ui.preview

import androidx.compose.runtime.Composable
import com.kitakkun.jetwhale.host.ui.JwTheme

/**
 * The theme a `@Preview` needs and the host supplies at runtime.
 *
 * The host wraps a plugin's `Content()` in [JwTheme] before calling it, so nothing in this plugin
 * installs a theme of its own. Outside the host -- in a preview or a UI test -- the Jw components
 * would have no [JwTheme.colors] to read, so the container puts one around them.
 */
@Composable
fun PreviewContainer(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    JwTheme(darkTheme = darkTheme) {
        content()
    }
}
