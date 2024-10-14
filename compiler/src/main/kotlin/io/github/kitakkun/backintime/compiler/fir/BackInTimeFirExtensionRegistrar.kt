package io.github.kitakkun.backintime.compiler.fir

import io.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
import io.github.kitakkun.backintime.compiler.fir.checkers.BackInTimeFirAdditionalCheckersExtension
import io.github.kitakkun.backintime.compiler.fir.extension.BackInTimeCompilerConfigurationProvider
import io.github.kitakkun.backintime.compiler.fir.extension.BackInTimeFirDeclarationGenerationExtension
import io.github.kitakkun.backintime.compiler.fir.extension.BackInTimeFirSupertypeGenerationExtension
import io.github.kitakkun.backintime.compiler.fir.matcher.DebuggableStateHolderPredicateMatcher
import io.github.kitakkun.backintime.compiler.fir.matcher.ValueContainerPredicateMatcher
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
