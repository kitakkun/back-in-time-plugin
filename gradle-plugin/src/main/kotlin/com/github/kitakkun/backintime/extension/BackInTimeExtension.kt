package com.github.kitakkun.backintime.extension

import com.github.kitakkun.backintime.plugin.extension.ValueContainerConfig
import groovy.lang.Closure
import kotlinx.serialization.Serializable

@Serializable
open class BackInTimeExtension(
    var enabled: Boolean = true,
    val valueContainers: MutableList<ValueContainerConfig> = mutableListOf(),
) {
    // KTS用
    fun valueContainers(configuration: ValueContainersScope.() -> Unit) {
        val scope = ValueContainersScope().apply(configuration)
        valueContainers.addAll(scope.containers)
    }

    // Groovy用
    fun valueContainers(closure: Closure<ValueContainersScope>) {
        val scope = ValueContainersScope()
        closure.delegate = scope
        closure.call()
        valueContainers.addAll(scope.containers)
    }
}
