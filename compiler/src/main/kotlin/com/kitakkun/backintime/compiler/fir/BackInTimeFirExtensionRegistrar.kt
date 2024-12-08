package com.kitakkun.backintime.compiler.fir

import com.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
import com.kitakkun.backintime.compiler.fir.checkers.BackInTimeFirAdditionalCheckersExtension
import com.kitakkun.backintime.compiler.fir.extension.BackInTimeCompilerConfigurationProvider
import com.kitakkun.backintime.compiler.fir.extension.BackInTimeFirDeclarationGenerationExtension
import com.kitakkun.backintime.compiler.fir.extension.BackInTimeFirSupertypeGenerationExtension
import com.kitakkun.backintime.compiler.fir.matcher.DebuggableStateHolderPredicateMatcher
import com.kitakkun.backintime.compiler.fir.matcher.ValueContainerPredicateMatcher
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
