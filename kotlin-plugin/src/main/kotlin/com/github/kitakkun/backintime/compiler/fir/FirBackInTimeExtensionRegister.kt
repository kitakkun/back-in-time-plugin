package com.github.kitakkun.backintime.compiler.fir

import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class BackInTimeFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::BackInTimeFirStateHolderManipulatorSuperTypeGenerationExtension
        +::FirManipulatorMethodsDeclarationGenerationExtension
        +::FirBackInTimePredicateMatcher
    }
}
