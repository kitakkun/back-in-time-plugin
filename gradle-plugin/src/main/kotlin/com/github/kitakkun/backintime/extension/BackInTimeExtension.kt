package com.github.kitakkun.backintime.extension

import com.github.kitakkun.backintime.plugin.extension.ValueContainerConfig
import kotlinx.serialization.Serializable

@Serializable
open class BackInTimeExtension(
    var enabled: Boolean = true,
    val valueContainers: MutableList<ValueContainerConfig> = mutableListOf(),
) {
    fun valueContainers(configuration: ValueContainersScope.() -> Unit) {
        val scope = ValueContainersScope().apply(configuration)
        valueContainers.addAll(scope.containers)
    }
}
