package com.github.kitakkun.backintime.evaluation.flux.architecture

class Dispatcher {
    private val listeners = mutableListOf<Store>()

    fun register(store: Store) {
        listeners.add(store)
    }

    fun unregister(store: Store) {
        listeners.remove(store)
    }

    fun dispatch(event: ActionEvent) {
        listeners.forEach { it.reduce(event) }
    }
}
