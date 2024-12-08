package com.kitakkun.backintime.compiler.k2.extension

import com.kitakkun.backintime.compiler.common.BackInTimeCompilerConfiguration
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
