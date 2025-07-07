package com.kitakkun.backintime.compiler.cli

import com.kitakkun.backintime.compiler.backend.BackInTimeIrGenerationExtension
import com.kitakkun.backintime.compiler.backend.MessageCollectorHolder
import com.kitakkun.backintime.compiler.k2.BackInTimeFirExtensionRegistrar
import com.kitakkun.backintime.compiler.yaml.BackInTimeYamlConfiguration
import com.kitakkun.backintime.compiler.yaml.BackInTimeYamlConfigurationParser
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import java.io.File

@Suppress("unused")
@OptIn(ExperimentalCompilerApi::class)
class BackInTimeCompilerRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val enabled = configuration[BackInTimeCommandLineProcessor.CONFIG_KEY_ENABLED] ?: true
        if (!enabled) return

        val yamlConfiguration = parseConfiguration(configuration)
        MessageCollectorHolder.messageCollector = configuration.get(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)

        FirExtensionRegistrarAdapter.registerExtension(BackInTimeFirExtensionRegistrar(yamlConfiguration))
        IrGenerationExtension.registerExtension(BackInTimeIrGenerationExtension(yamlConfiguration))
    }

    private fun parseConfiguration(configuration: CompilerConfiguration): BackInTimeYamlConfiguration {
        val yamlFilePath = configuration[BackInTimeCommandLineProcessor.CONFIG_KEY_CONFIG_FILE]
        return yamlFilePath?.let {
            BackInTimeYamlConfigurationParser().parse(File(it).readText())
        } ?: BackInTimeYamlConfiguration(trackableStateHolders = emptyList())
    }
}
