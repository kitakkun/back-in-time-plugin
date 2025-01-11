@file:Suppress("UNUSED")
@file:JsExport

package com.kitakkun.backintime.tooling.flipper

import kotlin.js.Promise

interface AppEvent {
    val payload: String
}

interface IncomingEvents {
    val appEvent: AppEvent
}

interface DebuggerAction {
    val payload: String
}

interface OutgoingEvents {
    fun debuggerEvent(action: DebuggerAction): Promise<Any>
}
