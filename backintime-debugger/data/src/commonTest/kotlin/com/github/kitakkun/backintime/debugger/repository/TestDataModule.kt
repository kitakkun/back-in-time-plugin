package com.github.kitakkun.backintime.debugger.repository

import com.github.kitakkun.backintime.debugger.data.di.sharedModule
import org.koin.dsl.module

val testDataModule = module {
    includes(sharedModule)
    factory { createTestSqlDriver() }
}
