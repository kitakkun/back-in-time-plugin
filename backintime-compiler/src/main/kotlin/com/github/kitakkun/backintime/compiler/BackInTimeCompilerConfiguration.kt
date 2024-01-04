package com.github.kitakkun.backintime.compiler

import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo

data class BackInTimeCompilerConfiguration(
    val enabled: Boolean,
    val valueContainers: List<ValueContainerClassInfo>,
)
