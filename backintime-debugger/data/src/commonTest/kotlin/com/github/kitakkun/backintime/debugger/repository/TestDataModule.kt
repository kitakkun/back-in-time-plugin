package com.github.kitakkun.backintime.debugger.repository

import com.github.kitakkun.backintime.debugger.data.BackInTimeDatabase
import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.listOfPropertyInfoAdapter
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import org.koin.dsl.module

val testDataModule = module {
    single<ClassInfoRepository> { ClassInfoRepositoryImpl(get()) }

    single {
        val driver = createTestSqlDriver()
        BackInTimeDatabase.Schema.create(driver)
        BackInTimeDatabase(
            driver = driver,
            classInfoAdapter = ClassInfo.Adapter(
                propertiesAdapter = listOfPropertyInfoAdapter,
            ),
        )
    }
    single { get<BackInTimeDatabase>().classInfoQueries }
}
