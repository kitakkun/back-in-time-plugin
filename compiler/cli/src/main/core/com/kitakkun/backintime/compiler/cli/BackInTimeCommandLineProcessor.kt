package com.kitakkun.backintime.compiler.cli

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@Suppress("UNUSED")
@OptIn(ExperimentalCompilerApi::class)
class BackInTimeCommandLineProcessor : CommandLineProcessor {
    companion object {
        private const val KEY_ENABLED = "enabled"
        private const val KEY_CONFIG_FILE = "config"
        val CONFIG_KEY_ENABLED = CompilerConfigurationKey.create<Boolean>(KEY_ENABLED)
        val CONFIG_KEY_CONFIG_FILE = CompilerConfigurationKey.create<String>(KEY_CONFIG_FILE)
    }

    override val pluginId: String = "com.kitakkun.backintime.compiler"
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = KEY_ENABLED,
            valueDescription = "true|false",
            description = "Whether BackInTime plugin is enabled or not.",
            required = false,
        ),
        CliOption(
            optionName = KEY_CONFIG_FILE,
            valueDescription = "path/to/backintime-config.yaml",
            description = "configuration file for backintime compiler",
            required = false,
        ),
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option.optionName) {
        KEY_ENABLED -> configuration.put(CONFIG_KEY_ENABLED, value.toBoolean())
        KEY_CONFIG_FILE -> configuration.put(CONFIG_KEY_CONFIG_FILE, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}
