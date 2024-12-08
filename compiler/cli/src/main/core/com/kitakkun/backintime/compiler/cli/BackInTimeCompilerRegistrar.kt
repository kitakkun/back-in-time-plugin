package com.kitakkun.backintime.compiler.cli

import com.kitakkun.backintime.compiler.backend.BackInTimeIrGenerationExtension
import com.kitakkun.backintime.compiler.backend.MessageCollectorHolder
import com.kitakkun.backintime.compiler.k2.BackInTimeFirExtensionRegistrar
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@Suppress("unused")
@OptIn(ExperimentalCompilerApi::class)
class BackInTimeCompilerRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val config = BackInTimeCompilerConfigurationProcessor().process(configuration)
        if (!config.enabled) return

        MessageCollectorHolder.messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        FirExtensionRegistrarAdapter.registerExtension(BackInTimeFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(BackInTimeIrGenerationExtension(config))
    }
}
