package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.plugin.BackInTimeCompilerOptionKey
import com.github.kitakkun.backintime.plugin.BackInTimePluginConsts
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

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
            optionName = BackInTimeCompilerOptionKey.CAPTURED_CALLS,
            valueDescription = "className1:functionName1,className2:functionName2,...(ex: androidx.lifecycle.MutableLiveData:<set-value>, androidx.compose.runtime.MutableState:<set-value>)",
            description = "Functions to be captured.",
            allowMultipleOccurrences = true,
            required = false,
        ),
        CliOption(
            optionName = BackInTimeCompilerOptionKey.VALUE_GETTERS,
            valueDescription = "className1:functionName1,className2:functionName2,...(ex: androidx.lifecycle.MutableLiveData:<get-value>, androidx.compose.runtime.MutableState:<get-value>)",
            description = "Value getters to be used.",
            allowMultipleOccurrences = true,
            required = false,
        ),
        CliOption(
            optionName = BackInTimeCompilerOptionKey.VALUE_SETTERS,
            valueDescription = "className1:functionName1,className2:functionName2,...(ex: androidx.lifecycle.MutableLiveData:<set-value>, androidx.compose.runtime.MutableState:<set-value>)",
            description = "Value setters to be used.",
            allowMultipleOccurrences = true,
            required = false,
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) = when (option.optionName) {
        BackInTimeCompilerOptionKey.ENABLED -> configuration.put(BackInTimeCompilerConfigurationKey.ENABLED, value.toBoolean())
        BackInTimeCompilerOptionKey.CAPTURED_CALLS -> configuration.appendList(BackInTimeCompilerConfigurationKey.CAPTURED_CALLS, value)
        BackInTimeCompilerOptionKey.VALUE_GETTERS -> configuration.appendList(BackInTimeCompilerConfigurationKey.VALUE_GETTERS, value)
        BackInTimeCompilerOptionKey.VALUE_SETTERS -> configuration.appendList(BackInTimeCompilerConfigurationKey.VALUE_SETTERS, value)
        else -> error("Unexpected config option ${option.optionName}")
    }
}
