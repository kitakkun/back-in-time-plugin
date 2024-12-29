package com.kitakkun.backintime.core.websocket.server

data class SessionInfo(
    val id: String,
    val host: String,
    val port: Int,
    val address: String,
)
