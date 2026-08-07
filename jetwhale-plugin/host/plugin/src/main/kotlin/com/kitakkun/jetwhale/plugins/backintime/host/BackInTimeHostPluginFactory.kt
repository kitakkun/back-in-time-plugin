package com.kitakkun.jetwhale.plugins.backintime.host

import com.kitakkun.jetwhale.host.sdk.JetWhaleHostPlugin
import com.kitakkun.jetwhale.host.sdk.JetWhaleHostPluginFactory

/**
 * Instantiated by the host through the fully-qualified name declared in
 * `META-INF/jetwhale/plugin-manifest.json`, so it needs a public no-arg constructor and must not be
 * renamed without updating the manifest.
 */
@Suppress("UNUSED")
class BackInTimeHostPluginFactory : JetWhaleHostPluginFactory {
    override fun createPlugin(): JetWhaleHostPlugin = BackInTimeHostPlugin()
}
