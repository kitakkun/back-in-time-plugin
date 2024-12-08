package com.kitakkun.backintime.compiler.cli

import com.kitakkun.backintime.compiler.common.BackInTimeCompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfiguration

class BackInTimeCompilerConfigurationProcessor {
    fun process(configuration: CompilerConfiguration) = BackInTimeCompilerConfiguration(
        enabled = configuration[BackInTimeCommandLineProcessor.CONFIG_KEY_ENABLED] ?: true,
    )
}
