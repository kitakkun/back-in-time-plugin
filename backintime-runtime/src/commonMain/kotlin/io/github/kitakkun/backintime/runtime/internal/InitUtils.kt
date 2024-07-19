@file:Suppress("UNUSED")

package io.github.kitakkun.backintime.runtime.internal

import io.github.kitakkun.backintime.runtime.connector.BackInTimeKtorWebSocketConnector
import io.github.kitakkun.backintime.runtime.getBackInTimeDebugService

@BackInTimeCompilerInternalApi
internal fun startBackInTimeDebugService(host: String, port: Int) {
    val service = getBackInTimeDebugService(useInUnitTest = false)
    service.setConnector(BackInTimeKtorWebSocketConnector(host, port))
    service.startService()
}
