package com.github.kitakkun.back_in_time

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class BackInTimeCommandLineProcessor : CommandLineProcessor {
    companion object {
        val KEY_ENABLED = CompilerConfigurationKey.create<Boolean>("my-plugin-enabled")
        val KEY_ANNOTATIONS = CompilerConfigurationKey.create<List<String>>("my-plugin-annotations")
    }

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
        "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
        "myPluginAnnotation" -> configuration.appendList(KEY_ANNOTATIONS, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}
