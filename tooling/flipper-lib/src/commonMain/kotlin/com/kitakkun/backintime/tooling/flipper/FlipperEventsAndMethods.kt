@file:JsExport

package com.kitakkun.backintime.tooling.flipper

import kotlin.js.Promise

interface AppEvent {
    val payload: String
}

interface IncomingEvents : EventsContract {
    val appEvent: AppEvent
}

interface DebuggerAction {
    val payload: String
}

interface OutgoingEvents : MethodsContract {
    fun debuggerEvent(action: DebuggerAction): Promise<Any>
}
