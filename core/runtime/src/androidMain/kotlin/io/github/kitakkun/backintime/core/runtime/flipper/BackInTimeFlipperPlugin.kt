package io.github.kitakkun.backintime.core.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperPlugin
import io.github.kitakkun.backintime.core.runtime.BackInTimeDebugService
import io.github.kitakkun.backintime.core.runtime.getBackInTimeDebugService
import io.github.kitakkun.backintime.core.runtime.internal.BackInTimeCompilerInternalApi

class BackInTimeFlipperPlugin : FlipperPlugin {
    @OptIn(BackInTimeCompilerInternalApi::class)
    private val service: BackInTimeDebugService = getBackInTimeDebugService()

    override fun getId(): String = "back-in-time"
    override fun runInBackground(): Boolean = true

    override fun onConnect(connection: FlipperConnection) {
        this.service.setConnector(BackInTimeFlipperConnector(connection))
        this.service.startService()
    }

    override fun onDisconnect() {
        this.service.stopService()
    }
}
