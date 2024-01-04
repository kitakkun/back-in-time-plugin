package com.github.kitakkun.backintime.compiler

import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

class BackInTimeCompilerConfigurationProcessor {
    fun process(configuration: CompilerConfiguration) = BackInTimeCompilerConfiguration(
        enabled = configuration[BackInTimeCompilerConfigurationKey.ENABLED] ?: false,
        valueContainers = configuration[BackInTimeCompilerConfigurationKey.VALUE_CONTAINER].orEmpty()
            .map { config ->
                val classId = ClassId.fromString(config.className.replace(".", "/"))

                ValueContainerClassInfo(
                    classId = classId,
                    capturedFunctionNames = config.captures.map { Name.guessByFirstCharacter(it) },
                    getterFunctionName = Name.guessByFirstCharacter(config.getter),
                    preSetterFunctionNames = config.preSetters.map { Name.guessByFirstCharacter(it) },
                    setterFunctionName = Name.guessByFirstCharacter(config.setter),
                    serializeItSelf = config.serializeItself,
                )
            },
    )
}
