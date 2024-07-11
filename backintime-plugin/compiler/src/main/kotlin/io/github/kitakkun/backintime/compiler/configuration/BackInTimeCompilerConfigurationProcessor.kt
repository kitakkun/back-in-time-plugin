package io.github.kitakkun.backintime.compiler.configuration

import io.github.kitakkun.backintime.compiler.valuecontainer.ValueContainerBuiltIns
import org.jetbrains.kotlin.config.CompilerConfiguration

class BackInTimeCompilerConfigurationProcessor {
    fun process(configuration: CompilerConfiguration) = BackInTimeCompilerConfiguration(
        enabled = configuration[BackInTimeCompilerConfigurationKey.ENABLED] ?: false,
        valueContainers = ValueContainerBuiltIns,
    )
}
