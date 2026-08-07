package com.kitakkun.backintime.tooling.core.shared

/**
 * What the debugger UI can ask the app being debugged to do.
 *
 * The JetWhale host plugin implements this over its agent messenger. It is scoped to a single
 * session — the plugin instance and the session share a lifetime — so no session id is passed in.
 */
interface BackInTimeDebuggerService {
    /**
     * Rewinds [instanceId] by assigning each `propertySignature -> jsonValue` pair back onto it.
     *
     * Fire-and-forget from the UI's point of view: the reply only says whether the instance was
     * still alive, and the inspector reflects that through the event history it receives anyway.
     */
    fun backInTime(instanceId: String, values: Map<String, String>)

    companion object {
        val Dummy = object : BackInTimeDebuggerService {
            override fun backInTime(instanceId: String, values: Map<String, String>) {}
        }
    }
}
