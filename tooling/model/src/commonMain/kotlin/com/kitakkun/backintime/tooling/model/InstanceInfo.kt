package com.kitakkun.backintime.tooling.model

import kotlin.js.JsExport

@JsExport
data class InstanceInfo(
    val uuid: String,
    val classSignature: String,
    val alive: Boolean,
    val registeredAt: Int,
) {
    @Suppress("UNUSED")
    fun copyWithUpdatingAlive(alive: Boolean) = copy(alive = alive)

    @Suppress("UNUSED")
    fun copyWithUpdatingClassSignature(newSignature: String) = copy(classSignature = newSignature)
}
