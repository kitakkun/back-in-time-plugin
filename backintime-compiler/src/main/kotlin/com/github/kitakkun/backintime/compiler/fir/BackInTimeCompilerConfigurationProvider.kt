package com.github.kitakkun.backintime.compiler.fir

import com.github.kitakkun.backintime.compiler.BackInTimeCompilerConfiguration
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
