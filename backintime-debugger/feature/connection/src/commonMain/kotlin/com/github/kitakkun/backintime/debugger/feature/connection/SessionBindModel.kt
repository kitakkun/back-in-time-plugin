package com.github.kitakkun.backintime.debugger.feature.connection

data class SessionBindModel(
    val host: String,
    val port: Int,
    val sessionId: String,
)
