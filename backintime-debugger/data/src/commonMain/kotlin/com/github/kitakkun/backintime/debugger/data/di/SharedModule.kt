package com.github.kitakkun.backintime.debugger.data.di

import app.cash.sqldelight.db.SqlDriver
import com.github.kitakkun.backintime.debugger.data.BackInTimeDatabase
import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.ClassInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepository
import com.github.kitakkun.backintime.debugger.data.repository.EventLogRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepository
import com.github.kitakkun.backintime.debugger.data.repository.InstanceRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.MethodCallInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.MethodCallInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.SessionInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepository
import com.github.kitakkun.backintime.debugger.data.repository.SettingsRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.ValueChangeInfoRepository
import com.github.kitakkun.backintime.debugger.data.repository.ValueChangeInfoRepositoryImpl
import com.github.kitakkun.backintime.debugger.data.repository.backInTimeDebugServiceEventAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfPropertyInfoAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfStringAdapter
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import com.github.kitakkun.backintime.debugger.data.server.IncomingEventProcessor
import com.github.kitakkun.backintime.debugger.data.server.IncomingEventProcessorImpl
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.EventLog
import com.github.kitakkun.backintime.debugger.database.Instance
import com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val sharedModule = module {
    // repositories
    single<ClassInfoRepository> { ClassInfoRepositoryImpl(get()) }
    single<InstanceRepository> { InstanceRepositoryImpl(get()) }
    single<EventLogRepository> { EventLogRepositoryImpl(get()) }
    single<SessionInfoRepository> { SessionInfoRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<MethodCallInfoRepository> { MethodCallInfoRepositoryImpl(get()) }
    single<ValueChangeInfoRepository> { ValueChangeInfoRepositoryImpl(get()) }

    // debugger services
    singleOf(::BackInTimeDebuggerService)
    factory<IncomingEventProcessor> { IncomingEventProcessorImpl(get(), get(), get(), get(), get()) }
    single<BackInTimeWebSocketServer> { BackInTimeWebSocketServer() }

    // sqldelight
    single {
        val driver: SqlDriver = get()
        BackInTimeDatabase.Schema.create(driver)
        BackInTimeDatabase(
            driver = driver,
            instanceAdapter = Instance.Adapter(
                childInstanceIdsAdapter = listOfStringAdapter,
            ),
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
    single { get<BackInTimeDatabase>().methodCallInfoQueries }
    single { get<BackInTimeDatabase>().valueChangeInfoQueries }
}
