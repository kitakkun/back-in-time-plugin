package com.github.kitakkun.backintime.runtime.flipper

import com.facebook.flipper.core.FlipperConnection
import com.facebook.flipper.core.FlipperPlugin
import com.github.kitakkun.backintime.runtime.BackInTimeDebugServiceImpl
import com.github.kitakkun.backintime.runtime.connector.BackInTimeConnector

class BackInTimeFlipperPlugin : FlipperPlugin {
    private var connector: BackInTimeConnector? = null
    private val service = BackInTimeDebugServiceImpl

    override fun getId(): String = "back-in-time"
    override fun runInBackground(): Boolean = true

    override fun onConnect(connection: FlipperConnection) {
        this.connector = BackInTimeFlipperConnector(connection)
        this.service.setConnector(connector = this.connector!!)
        this.service.startService()
    }

    override fun onDisconnect() {
        this.service.stopService()
        this.connector = null
    }
}
