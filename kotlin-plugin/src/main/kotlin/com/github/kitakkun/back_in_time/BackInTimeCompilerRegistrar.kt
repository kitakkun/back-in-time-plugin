package com.github.kitakkun.back_in_time

import com.github.kitakkun.back_in_time.backend.BackInTimeIrGenerationExtension
import com.github.kitakkun.back_in_time.fir.BackInTimeFirExtensionRegistrar
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@Suppress("unused")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class BackInTimeCompilerRegistrar : CompilerPluginRegistrar() {
    // For now, not intended to support K2.
    override val supportsK2: Boolean get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[BackInTimeCommandLineProcessor.KEY_ENABLED] == false) {
            return
        }

        FirExtensionRegistrarAdapter.registerExtension(BackInTimeFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(BackInTimeIrGenerationExtension())
    }
}

