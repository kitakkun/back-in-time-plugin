package com.github.kitakkun.backintime

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class BackInTimeCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = "back-in-time-plugin"
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = "enabled",
            valueDescription = "true|false",
            description = "Whether MyPlugin is enabled or not.",
        ),
        CliOption(
            optionName = "myPluginAnnotation",
            valueDescription = "annotation",
            description = "Annotation to be processed by MyPlugin.",
            allowMultipleOccurrences = true,
        ),
    )

    override fun processOption(
        option: AbstractCliOption,
        value: String,
        configuration: CompilerConfiguration,
    ) = when (option.optionName) {
        "enabled" -> configuration.put(BackInTimeCompilerConfigurationKey.ENABLED, value.toBoolean())
        "myPluginAnnotation" -> configuration.appendList(BackInTimeCompilerConfigurationKey.ANNOTATIONS, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}
