package com.github.kitakkun.back_in_time.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class BackInTimeFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::FirStateHolderManipulatorDeclarationGenerationExtension
    }
}
