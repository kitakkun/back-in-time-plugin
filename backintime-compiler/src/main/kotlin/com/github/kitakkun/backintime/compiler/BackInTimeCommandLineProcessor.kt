package com.github.kitakkun.backintime.compiler

import com.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfigurationKey
import com.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import com.github.kitakkun.backintime.plugin.BackInTimePluginConsts
import com.github.kitakkun.backintime.plugin.extension.ValueContainerConfig
import com.google.auto.service.AutoService
import kotlinx.serialization.json.Json
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import java.util.Base64

@Suppress("UNUSED")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CommandLineProcessor::class)
class BackInTimeCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String = BackInTimePluginConsts.pluginId
    override val pluginOptions: Collection<AbstractCliOption> = listOf(
        CliOption(
            optionName = BackInTimeCompilerOptionKey.ENABLED,
            valueDescription = "true|false",
            description = "Whether BackInTime plugin is enabled or not.",
            required = false,
        ),
        CliOption(
            optionName = BackInTimeCompilerOptionKey.VALUE_CONTAINER,
            valueDescription = "configurable via container dsl",
            description = "predefined debuggable value-container class",
            allowMultipleOccurrences = true,
            required = false,
        ),
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option.optionName) {
        BackInTimeCompilerOptionKey.ENABLED -> configuration.put(BackInTimeCompilerConfigurationKey.ENABLED, value.toBoolean())
        BackInTimeCompilerOptionKey.VALUE_CONTAINER -> {
            val decodedValue = String(Base64.getDecoder().decode(value), Charsets.UTF_8)
            val config = Json.decodeFromString<ValueContainerConfig>(decodedValue)
            configuration.appendList(BackInTimeCompilerConfigurationKey.VALUE_CONTAINER, config)
        }

        else -> error("Unexpected config option ${option.optionName}")
    }
}
