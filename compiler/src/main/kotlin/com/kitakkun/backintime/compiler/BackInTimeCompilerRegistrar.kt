package com.kitakkun.backintime.compiler

import com.google.auto.service.AutoService
import com.kitakkun.backintime.compiler.backend.BackInTimeIrGenerationExtension
import com.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfigurationProcessor
import com.kitakkun.backintime.compiler.fir.BackInTimeFirExtensionRegistrar
import com.kitakkun.backintime.compiler.util.MessageCollectorHolder
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@Suppress("unused")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class BackInTimeCompilerRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val config = BackInTimeCompilerConfigurationProcessor().process(configuration)
        if (!config.enabled) return

        MessageCollectorHolder.messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        FirExtensionRegistrarAdapter.registerExtension(BackInTimeFirExtensionRegistrar(config))
        IrGenerationExtension.registerExtension(BackInTimeIrGenerationExtension(config))
    }
}
