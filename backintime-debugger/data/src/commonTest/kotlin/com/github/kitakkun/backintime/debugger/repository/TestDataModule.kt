package com.github.kitakkun.backintime.debugger.repository

import com.github.kitakkun.backintime.debugger.data.di.SharedModule
import org.koin.dsl.module
import org.koin.ksp.generated.module

val testDataModule = module {
    includes(SharedModule().module)
    factory { createTestSqlDriver() }
}
