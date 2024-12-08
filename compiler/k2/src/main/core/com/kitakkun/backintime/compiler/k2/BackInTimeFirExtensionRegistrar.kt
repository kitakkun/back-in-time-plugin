package com.kitakkun.backintime.compiler.k2

import com.kitakkun.backintime.compiler.common.BackInTimeCompilerConfiguration
import com.kitakkun.backintime.compiler.k2.checkers.BackInTimeFirAdditionalCheckersExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeCompilerConfigurationProvider
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeFirDeclarationGenerationExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeFirSupertypeGenerationExtension
import com.kitakkun.backintime.compiler.k2.matcher.DebuggableStateHolderPredicateMatcher
import com.kitakkun.backintime.compiler.k2.matcher.ValueContainerPredicateMatcher
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
