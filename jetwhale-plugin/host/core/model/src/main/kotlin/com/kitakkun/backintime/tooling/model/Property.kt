package com.kitakkun.backintime.tooling.model

data class Property(
    val signature: String,
    val name: String,
    val type: String,
    val totalEvents: Int,
    val debuggable: Boolean,
    val isInherited: Boolean,
    val parentClassSignature: String,
)
