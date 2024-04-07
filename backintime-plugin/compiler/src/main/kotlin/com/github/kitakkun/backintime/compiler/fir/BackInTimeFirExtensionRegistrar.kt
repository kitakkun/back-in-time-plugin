package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
import com.github.kitakkun.backintime.compiler.fir.checkers.BackInTimeFirAdditionalCheckersExtension
import com.github.kitakkun.backintime.compiler.fir.extension.BackInTimeCompilerConfigurationProvider
import com.github.kitakkun.backintime.compiler.fir.extension.BackInTimeFirDeclarationGenerationExtension
import com.github.kitakkun.backintime.compiler.fir.extension.BackInTimeFirSupertypeGenerationExtension
import com.github.kitakkun.backintime.compiler.fir.matcher.DebuggableStateHolderPredicateMatcher
import com.github.kitakkun.backintime.compiler.fir.matcher.ValueContainerPredicateMatcher
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
