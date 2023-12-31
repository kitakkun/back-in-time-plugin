package com.github.kitakkun.backintime.extension

import com.github.kitakkun.backintime.plugin.extension.ValueContainerConfig

class ValueContainersScope {
    var containers: MutableList<ValueContainerConfig> = mutableListOf()
        private set

    fun container(configuration: ValueContainerConfig.() -> Unit) {
        val config = ValueContainerConfig().apply(configuration)
        this.containers.add(config)
    }

    fun androidValueContainers() {
        container {
            className = "androidx.lifecycle.MutableLiveData"
            captures = listOf("postValue", "setValue")
            getter = "getValue"
            setter = "postValue"
        }
        container {
            className = "androidx.compose.runtime.MutableState"
            captures = listOf("<set-value>")
            getter = "<get-value>"
            setter = "<set-value>"
        }
        container {
            className = "kotlinx.coroutines.flow.MutableStateFlow"
            captures = listOf("<set-value>", "update", "updateAndGet", "getAndUpdate", "emit", "tryEmit")
            getter = "<get-value>"
            setter = "<set-value>"
        }
    }
}

