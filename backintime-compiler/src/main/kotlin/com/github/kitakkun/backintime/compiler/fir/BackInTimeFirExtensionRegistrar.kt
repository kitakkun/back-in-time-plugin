package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeCompilerConfiguration
import com.github.kitakkun.backintime.compiler.fir.checkers.BackInTimeFirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class BackInTimeFirExtensionRegistrar(
    private val config: BackInTimeCompilerConfiguration,
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +::BackInTimeFirSupertypeGenerationExtension
        +::BackInTimeFirDeclarationGenerationExtension

        +::DebuggableStateHolderPredicateMatcher
        +::ValueContainerPredicateMatcher

        +::BackInTimeFirAdditionalCheckersExtension
        +BackInTimeCompilerConfigurationProvider.getFactory(config)
    }
}
