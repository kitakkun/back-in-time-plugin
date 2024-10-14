package io.github.kitakkun.backintime.compiler.fir.extension

import io.github.kitakkun.backintime.compiler.configuration.BackInTimeCompilerConfiguration
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent

class BackInTimeCompilerConfigurationProvider(
    session: FirSession,
    val config: BackInTimeCompilerConfiguration,
) : FirExtensionSessionComponent(session) {
    companion object {
        fun getFactory(config: BackInTimeCompilerConfiguration) = { session: FirSession -> BackInTimeCompilerConfigurationProvider(session, config) }
    }
}

val FirSession.backInTimeCompilerConfigurationProvider: BackInTimeCompilerConfigurationProvider by FirSession.sessionComponentAccessor()
