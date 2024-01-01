package com.github.kitakkun.backintime.plugin.extension

import kotlinx.serialization.Serializable

@Serializable
data class ValueContainerConfig(
    var className: String = "",
    var captures: List<String> = emptyList(),
    var getter: String = "",
    var preSetters: List<String> = emptyList(),
    var setter: String = "",
    var serializeItself: Boolean = false,
)

