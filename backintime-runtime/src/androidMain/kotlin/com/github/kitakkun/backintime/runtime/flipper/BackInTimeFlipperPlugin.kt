package com.github.kitakkun.backintime.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.runtime.BackInTimeDebugService
import com.github.kitakkun.backintime.runtime.getBackInTimeDebugService
import com.github.kitakkun.backintime.runtime.internal.BackInTimeCompilerInternalApi

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
