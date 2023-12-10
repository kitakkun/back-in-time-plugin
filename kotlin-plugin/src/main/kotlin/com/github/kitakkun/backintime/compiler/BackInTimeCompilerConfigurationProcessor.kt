package com.github.kitakkun.backintime.compiler

import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

class BackInTimeCompilerConfigurationProcessor {
    fun process(configuration: CompilerConfiguration) = BackInTimeCompilerConfiguration(
        enabled = configuration[BackInTimeCompilerConfigurationKey.ENABLED] ?: false,
        capturedCallableIds = configuration[BackInTimeCompilerConfigurationKey.CAPTURED_CALLS].orEmpty().map {
            val (className, functionName) = it.replace(".", "/").split(":")
            CallableId(ClassId.fromString(className), Name.guessByFirstCharacter(functionName))
        }.toSet(),
        valueGetterCallableIds = configuration[BackInTimeCompilerConfigurationKey.VALUE_GETTERS].orEmpty().map {
            val (className, functionName) = it.replace(".", "/").split(":")
            CallableId(ClassId.fromString(className), Name.guessByFirstCharacter(functionName))
        }.toSet(),
        valueSetterCallableIds = configuration[BackInTimeCompilerConfigurationKey.VALUE_SETTERS].orEmpty().map {
            val (className, functionName) = it.replace(".", "/").split(":")
            CallableId(ClassId.fromString(className), Name.guessByFirstCharacter(functionName))
        }.toSet(),
    )
}
