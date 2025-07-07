package com.kitakkun.backintime.compiler.k2.extension

import com.kitakkun.backintime.compiler.yaml.BackInTimeYamlConfiguration
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.extensions.FirExtensionSessionComponent

class BackInTimeYamlConfigurationProvider(
    val yamlConfiguration: BackInTimeYamlConfiguration,
    session: FirSession,
) : FirExtensionSessionComponent(session) {
    companion object {
        fun createFactory(yamlConfiguration: BackInTimeYamlConfiguration): (FirSession) -> BackInTimeYamlConfigurationProvider {
            return { session: FirSession ->
                BackInTimeYamlConfigurationProvider(yamlConfiguration, session)
            }
        }
    }
}

val FirSession.yamlConfigurationProvider by FirSession.sessionComponentAccessor<BackInTimeYamlConfigurationProvider>()
