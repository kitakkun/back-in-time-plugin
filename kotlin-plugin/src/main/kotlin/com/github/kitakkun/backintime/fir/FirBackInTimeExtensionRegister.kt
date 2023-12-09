package com.github.kitakkun.backintime.fir

import com.github.kitakkun.backintime.fir.checkers.BackInTimeFirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class BackInTimeFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::BackInTimeFirStateHolderManipulatorSuperTypeGenerationExtension
        +::FirManipulatorMethodsDeclarationGenerationExtension
        +::BackInTimeFirAdditionalCheckersExtension
    }
}
