package io.github.kitakkun.backintime.compiler

import com.google.auto.service.AutoService
import io.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfigurationKey
import io.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import io.github.kitakkun.backintime.plugin.BackInTimePluginConsts
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@Suppress("UNUSED")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class BackInTimeCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = BackInTimePluginConsts.PLUGIN_ID
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = BackInTimeCompilerOptionKey.ENABLED,
            valueDescription = "true|false",
            description = "Whether BackInTime plugin is enabled or not.",
            required = false,
        ),
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option.optionName) {
        BackInTimeCompilerOptionKey.ENABLED -> configuration.put(BackInTimeCompilerConfigurationKey.ENABLED, value.toBoolean())
        else -> error("Unexpected config option ${option.optionName}")
    }
}
