package com.github.kitakkun.backintime.debugger

import com.github.kitakkun.backintime.debugger.data.di.dataModule
import com.github.kitakkun.backintime.debugger.feature.connection.connectionFeatureModule
import com.github.kitakkun.backintime.debugger.feature.log.logFeatureModule
import com.github.kitakkun.backintime.debugger.feature.settings.settingsFeatureModule
import com.github.kitakkun.backintime.debugger.featurecommon.featureCommonModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            appModule,
            dataModule,
            connectionFeatureModule,
            logFeatureModule,
            settingsFeatureModule,
            featureCommonModule,
        )
    }
}
