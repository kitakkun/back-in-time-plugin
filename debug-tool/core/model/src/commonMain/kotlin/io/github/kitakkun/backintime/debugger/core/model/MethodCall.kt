package io.github.kitakkun.backintime.debugger.core.model

data class MethodCall(
    val valueChanges: List<ValueChange>,
    val calledAt: Long,
)
