package com.github.kitakkun.back_in_time

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.jvm.extensions.ClassGeneratorExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirDeclarationGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

@Suppress("unused")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class BackInTimeCompilerRegistrar : CompilerPluginRegistrar() {
    // For now, not intended to support K2.
    override val supportsK2: Boolean get() = false

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        if (configuration[BackInTimeCommandLineProcessor.KEY_ENABLED] == false) {
            return
        }

        val annotations = configuration[BackInTimeCommandLineProcessor.KEY_ANNOTATIONS]
            ?: error("MyPlugin is enabled but no annotations are specified.")
        // MyIrGenerationExtension は次のステップで実装
        IrGenerationExtension.registerExtension(DebuggableStateHolderIrGenerationExtension())
    }
}
