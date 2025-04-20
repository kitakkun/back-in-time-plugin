package com.kitakkun.backintime.core.runtime.internal

import com.kitakkun.backintime.core.runtime.BackInTimeDebugService
import com.kitakkun.backintime.core.runtime.connector.BackInTimeKtorWebSocketConnector
import com.kitakkun.backintime.core.runtime.getBackInTimeDebugService

@Suppress("UNUSED")
@BackInTimeCompilerInternalApi
fun registerBackInTimeEntryPoint(host: String, port: Int) {
    @OptIn(BackInTimeCompilerInternalApi::class)
    val service: BackInTimeDebugService = getBackInTimeDebugService()
    service.setConnector(BackInTimeKtorWebSocketConnector(host = host, port = port))
    service.startService()
}
