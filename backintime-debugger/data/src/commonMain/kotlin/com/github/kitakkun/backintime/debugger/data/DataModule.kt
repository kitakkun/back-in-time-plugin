package com.github.kitakkun.backintime.debugger.data

import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepository
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepository
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.backInTimeDebugServiceEventAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfPropertyInfoAdapter
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.EventLog
import org.koin.dsl.module

val dataModule = module {
    // repositories
    single<ClassInfoRepository> { ClassInfoRepositoryImpl(get()) }
    single<InstanceRepository> { InstanceRepositoryImpl(get()) }
    single<EventLogRepository> { EventLogRepositoryImpl(get()) }
    single<SessionInfoRepository> { SessionInfoRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl() }

    // sqldelight
    single {
        val driver = createSqlDriver()
        BackInTimeDatabase.Schema.create(driver)
        BackInTimeDatabase(
            driver = driver,
            classInfoAdapter = ClassInfo.Adapter(
                propertiesAdapter = listOfPropertyInfoAdapter,
            ),
            eventLogAdapter = EventLog.Adapter(
                payloadAdapter = backInTimeDebugServiceEventAdapter,
            ),
        )
    }
    single { get<BackInTimeDatabase>().sessionInfoQueries }
    single { get<BackInTimeDatabase>().classInfoQueries }
    single { get<BackInTimeDatabase>().instanceQueries }
    single { get<BackInTimeDatabase>().eventLogQueries }
}
