package io.github.kitakkun.backintime.debugger.core.model

data class Instance(
    val id: String,
    val classInfo: ClassInfo,
    val isAlive: Boolean,
    val events: List<MethodCall>,
)
