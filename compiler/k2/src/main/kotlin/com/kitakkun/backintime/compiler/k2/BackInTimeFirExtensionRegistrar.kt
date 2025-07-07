package com.kitakkun.backintime.compiler.k2

import com.kitakkun.backintime.compiler.k2.checkers.BackInTimeFirAdditionalCheckersExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeFirDeclarationGenerationExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeFirSupertypeGenerationExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeYamlConfigurationProvider
import com.kitakkun.backintime.compiler.yaml.BackInTimeYamlConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class BackInTimeFirExtensionRegistrar(
    val yamlConfiguration: BackInTimeYamlConfiguration,
) : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        +BackInTimeYamlConfigurationProvider.createFactory(yamlConfiguration)

        +::BackInTimeFirSupertypeGenerationExtension
        +::BackInTimeFirDeclarationGenerationExtension

        +::BackInTimeFirAdditionalCheckersExtension
    }
}
