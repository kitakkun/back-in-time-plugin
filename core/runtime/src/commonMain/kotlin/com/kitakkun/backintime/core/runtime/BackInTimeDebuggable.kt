package com.kitakkun.backintime.core.runtime

interface BackInTimeDebuggable {
    val backInTimeInstanceUUID: String
    val backInTimeInitializedPropertyMap: MutableMap<String, Boolean>

    fun forceSetValue(propertySignature: String, jsonValue: String)
}
