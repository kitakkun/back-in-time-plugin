package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.name.CallableId

data class BackInTimeCompilerConfiguration(
    val enabled: Boolean,
    val capturedCallableIds: Set<CallableId>,
    val valueGetterCallableIds: Set<CallableId>,
    val valueSetterCallableIds: Set<CallableId>,
)
