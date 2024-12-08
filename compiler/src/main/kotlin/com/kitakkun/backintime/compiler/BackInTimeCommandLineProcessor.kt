package com.kitakkun.backintime.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@Suppress("UNUSED")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class BackInTimeCommandLineProcessor : CommandLineProcessor {
    companion object {
        private const val KEY_ENABLED = "enabled"
        val CONFIG_KEY_ENABLED = CompilerConfigurationKey.create<Boolean>(KEY_ENABLED)
    }

    override val pluginId: String = ""
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = KEY_ENABLED,
            valueDescription = "true|false",
            description = "Whether BackInTime plugin is enabled or not.",
            required = false,
        ),
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option.optionName) {
        KEY_ENABLED -> configuration.put(CONFIG_KEY_ENABLED, value.toBoolean())
        else -> error("Unexpected config option ${option.optionName}")
    }
}
