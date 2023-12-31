package com.github.kitakkun.backintime.compiler

import com.github.kitakkun.backintime.compiler.backend.ValueContainerClassInfo
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.name.CallableId
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
                    capturedCallableIds = config.captures.map { CallableId(classId, Name.guessByFirstCharacter(it)) },
                    valueGetter = CallableId(classId, Name.guessByFirstCharacter(config.getter)),
                    valueSetter = CallableId(classId, Name.guessByFirstCharacter(config.setter)),
                )
            },
    )
}
