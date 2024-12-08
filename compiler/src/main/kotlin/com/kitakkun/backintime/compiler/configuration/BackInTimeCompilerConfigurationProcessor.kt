package com.kitakkun.backintime.compiler.configuration

import com.kitakkun.backintime.compiler.BackInTimeCommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration

class BackInTimeCompilerConfigurationProcessor {
    fun process(configuration: CompilerConfiguration) = BackInTimeCompilerConfiguration(
        enabled = configuration[BackInTimeCommandLineProcessor.CONFIG_KEY_ENABLED] ?: true,
    )
}
