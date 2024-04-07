package com.github.kitakkun.backintime.compiler.configuration

import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo

data class BackInTimeCompilerConfiguration(
    val enabled: Boolean,
    val valueContainers: List<ValueContainerClassInfo>,
)
