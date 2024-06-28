package com.github.kitakkun.backintime.debugger

import com.github.kitakkun.backintime.debugger.data.di.DataModule
import com.github.kitakkun.backintime.debugger.feature.connection.ConnectionFeatureModule
import com.github.kitakkun.backintime.debugger.feature.instance.di.InstanceFeatureModule
import com.github.kitakkun.backintime.debugger.feature.log.LogFeatureModule
import com.github.kitakkun.backintime.debugger.feature.settings.SettingsFeatureModule
import com.github.kitakkun.backintime.debugger.featurecommon.FeatureCommonModule
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module

@Module(
    includes = [
        DataModule::class,
        InstanceFeatureModule::class,
        ConnectionFeatureModule::class,
        LogFeatureModule::class,
        SettingsFeatureModule::class,
        FeatureCommonModule::class,
    ],
)
@ComponentScan("com.github.kitakkun.backintime.debugger")
class AppModule
