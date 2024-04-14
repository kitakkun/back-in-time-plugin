package com.github.kitakkun.backintime.debugger.data.di

import com.github.kitakkun.backintime.debugger.data.driver.createSqlDriver
import org.koin.dsl.module

val dataModule = module {
    includes(sharedModule)
    factory { createSqlDriver() }
}
