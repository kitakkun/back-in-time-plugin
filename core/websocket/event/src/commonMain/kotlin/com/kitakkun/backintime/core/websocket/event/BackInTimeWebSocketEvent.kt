package com.kitakkun.backintime.core.websocket.event

import kotlinx.serialization.Serializable
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
@Serializable
sealed interface BackInTimeWebSocketEvent
