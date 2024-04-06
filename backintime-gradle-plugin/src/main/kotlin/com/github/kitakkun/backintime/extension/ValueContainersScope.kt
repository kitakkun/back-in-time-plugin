package com.github.kitakkun.backintime.extension

import com.github.kitakkun.backintime.plugin.extension.ValueContainerConfig
import groovy.lang.Closure

class ValueContainersScope {
    var containers: MutableList<ValueContainerConfig> = mutableListOf()
        private set

    // KTS用
    fun container(configuration: ValueContainerConfig.() -> Unit) {
        val config = ValueContainerConfig().apply(configuration)
        this.containers.add(config)
    }

    // Groovy用
    fun container(closure: Closure<ValueContainerConfig>) {
        val config = ValueContainerConfig()
        closure.delegate = config
        closure.call()
        this.containers.add(config)
    }

    fun androidValueContainers() {
        container {
            className = "androidx/lifecycle/MutableLiveData"
            captures = listOf("postValue", "setValue")
            getter = "getValue"
            setter = "postValue"
        }
        container {
            className = "androidx/compose/runtime/MutableState"
            captures = listOf("<set-value>")
            getter = "<get-value>"
            setter = "<set-value>"
        }
        container {
            className = "kotlinx/coroutines/flow/MutableStateFlow"
            captures = listOf("<set-value>", "update", "updateAndGet", "getAndUpdate", "emit", "tryEmit")
            getter = "<get-value>"
            setter = "<set-value>"
        }
    }

    fun collections() {
        container {
            className = "kotlin/collections/MutableList"
            captures = listOf("add", "addAll", "clear", "remove", "removeAll", "removeAt", "set", "replaceAll")
            getter = "<this>"
            preSetters = listOf("clear")
            setter = "addAll"
            serializeItself = true
        }
        container {
            className = "kotlin/collections/MutableSet"
            captures = listOf("add", "addAll", "clear", "remove", "removeAll")
            getter = "<this>"
            preSetters = listOf("clear")
            setter = "addAll"
            serializeItself = true
        }
        container {
            className = "kotlin/collections/MutableMap"
            captures = listOf("clear", "put", "putAll", "remove", "set", "replace", "replaceAll")
            getter = "<this>"
            preSetters = listOf("clear")
            setter = "putAll"
            serializeItself = true
        }
    }

    fun composeMutableStates() {
        container {
            className = "androidx/compose/runtime/MutableState"
            captures = listOf("<set-value>")
            getter = "<get-value>"
            setter = "<set-value>"
        }
        container {
            className = "androidx/compose/runtime/MutableIntState"
            captures = listOf("<set-value>", "<set-intValue>")
            getter = "<get-value>"
            setter = "<set-value>"
            serializeAs = "kotlin/Int"
        }
        container {
            className = "androidx/compose/runtime/MutableLongState"
            captures = listOf("<set-value>", "<set-longValue>")
            getter = "<get-value>"
            setter = "<set-value>"
            serializeAs = "kotlin/Long"
        }
        container {
            className = "androidx/compose/runtime/MutableFloatState"
            captures = listOf("<set-value>", "<set-floatValue>")
            getter = "<get-value>"
            setter = "<set-value>"
            serializeAs = "kotlin/Float"
        }
        container {
            className = "androidx/compose/runtime/MutableDoubleState"
            captures = listOf("<set-value>", "<set-doubleValue>")
            getter = "<get-value>"
            setter = "<set-value>"
            serializeAs = "kotlin/Double"
        }
        container {
            className = "androidx/compose/runtime/snapshots/SnapshotStateList"
            captures = listOf("add", "addAll", "clear", "remove", "removeAll", "removeAt", "set", "replaceAll")
            getter = "<this>"
            preSetters = listOf("clear")
            setter = "addAll"
            serializeItself = true
            serializeAs = "kotlin/collections/List"
        }
        container {
            className = "androidx/compose/runtime/snapshots/SnapshotStateMap"
            captures = listOf("clear", "put", "putAll", "remove", "set")
            getter = "<this>"
            preSetters = listOf("clear")
            setter = "putAll"
            serializeItself = true
            serializeAs = "kotlin/collections/Map"
        }
    }
}
