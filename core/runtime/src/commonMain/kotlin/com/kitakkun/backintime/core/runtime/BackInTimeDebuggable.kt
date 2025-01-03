package com.kitakkun.backintime.core.runtime

interface BackInTimeDebuggable {
    val backInTimeInstanceUUID: String
    val backInTimeInitializedPropertyMap: MutableMap<String, Boolean>

    fun forceSetValue(propertyOwnerClassFqName: String, propertyName: String, value: String)
}
