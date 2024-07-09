package io.github.kitakkun.backintime.compiler.configuration

import org.jetbrains.kotlin.config.CompilerConfiguration

class BackInTimeCompilerConfigurationProcessor {
    fun process(configuration: CompilerConfiguration) = BackInTimeCompilerConfiguration(
        enabled = configuration[BackInTimeCompilerConfigurationKey.ENABLED] ?: false,
    )
}
