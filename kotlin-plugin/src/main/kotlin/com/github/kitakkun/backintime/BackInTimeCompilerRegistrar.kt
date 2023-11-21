package com.github.kitakkun.backintime

import com.github.kitakkun.backintime.backend.BackInTimeIrGenerationExtension
import com.github.kitakkun.backintime.fir.BackInTimeFirExtensionRegistrar
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name

@Suppress("unused")
@OptIn(ExperimentalCompilerApi::class)
@AutoService(CompilerPluginRegistrar::class)
class BackInTimeCompilerRegistrar : CompilerPluginRegistrar() {
    override val supportsK2: Boolean get() = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val enabled = configuration[BackInTimeCompilerConfigurationKey.ENABLED] ?: false
        val capturedCallableIds = configuration[BackInTimeCompilerConfigurationKey.CAPTURED_CALLS].orEmpty().map {
            val (className, functionName) = it.split(":")
            CallableId(ClassId.fromString(className), Name.identifier(functionName))
        }
        val valueGetterCallableIds = configuration[BackInTimeCompilerConfigurationKey.VALUE_GETTERS].orEmpty().map {
            val (className, functionName) = it.split(":")
            CallableId(ClassId.fromString(className), Name.identifier(functionName))
        }

        if (!enabled) return

        MessageCollectorHolder.messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        FirExtensionRegistrarAdapter.registerExtension(BackInTimeFirExtensionRegistrar())
        IrGenerationExtension.registerExtension(BackInTimeIrGenerationExtension(
            capturedCallableIds = capturedCallableIds,
            valueGetterCallableIds = valueGetterCallableIds,
        ))
    }
}

