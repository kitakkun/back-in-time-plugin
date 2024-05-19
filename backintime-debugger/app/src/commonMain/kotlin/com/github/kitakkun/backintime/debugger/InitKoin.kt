package com.github.kitakkun.backintime.debugger

import com.github.kitakkun.backintime.debugger.data.di.dataModule
import com.github.kitakkun.backintime.debugger.feature.instance.di.instanceFeatureModule
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(
            dataModule,
            instanceFeatureModule,
        )
    }
}
