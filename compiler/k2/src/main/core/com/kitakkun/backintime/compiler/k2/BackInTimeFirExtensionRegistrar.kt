package com.kitakkun.backintime.compiler.k2

import com.kitakkun.backintime.compiler.k2.api.VersionSpecificAPI
import com.kitakkun.backintime.compiler.k2.api.VersionSpecificAPIImpl
import com.kitakkun.backintime.compiler.k2.checkers.BackInTimeFirAdditionalCheckersExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeFirDeclarationGenerationExtension
import com.kitakkun.backintime.compiler.k2.extension.BackInTimeFirSupertypeGenerationExtension
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar

class BackInTimeFirExtensionRegistrar : FirExtensionRegistrar() {
    override fun ExtensionRegistrarContext.configurePlugin() {
        VersionSpecificAPI.INSTANCE = VersionSpecificAPIImpl

        +::BackInTimeFirSupertypeGenerationExtension
        +::BackInTimeFirDeclarationGenerationExtension

        +::BackInTimeFirAdditionalCheckersExtension
    }
}
