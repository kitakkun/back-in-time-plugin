package io.github.kitakkun.backintime.evaluation.flux.architecture

interface Store {
    fun reduce(event: ActionEvent)
}
