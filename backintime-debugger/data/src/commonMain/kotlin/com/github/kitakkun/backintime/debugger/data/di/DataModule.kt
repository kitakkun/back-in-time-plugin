package com.github.kitakkun.backintime.debugger.data.di

import com.github.kitakkun.backintime.debugger.data.BackInTimeDatabase
import com.github.kitakkun.backintime.debugger.data.driver.createSqlDriver
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
import com.github.kitakkun.backintime.debugger.data.repository.backInTimeDebugServiceEventAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfPropertyInfoAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfValueChangeInfoAdapter
import com.github.kitakkun.backintime.debugger.data.server.BackInTimeDebuggerService
import com.github.kitakkun.backintime.debugger.data.server.IncomingEventProcessor
import com.github.kitakkun.backintime.debugger.data.server.IncomingEventProcessorImpl
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.EventLog
import com.github.kitakkun.backintime.debugger.database.MethodCallInfo
import com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import org.koin.dsl.module

val dataModule = module {
    // repositories
    single<ClassInfoRepository> { ClassInfoRepositoryImpl(get()) }
    single<InstanceRepository> { InstanceRepositoryImpl(get()) }
    single<EventLogRepository> { EventLogRepositoryImpl(get()) }
    single<SessionInfoRepository> { SessionInfoRepositoryImpl(get()) }
    single<SettingsRepository> { SettingsRepositoryImpl() }
    single<MethodCallInfoRepository> { MethodCallInfoRepositoryImpl(get()) }

    // debugger services
    single<BackInTimeDebuggerService> { BackInTimeDebuggerService(get(), get()) }
    factory<IncomingEventProcessor> { IncomingEventProcessorImpl(get(), get(), get(), get()) }
    single<BackInTimeWebSocketServer> { BackInTimeWebSocketServer() }

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
            methodCallInfoAdapter = MethodCallInfo.Adapter(
                valueChangesAdapter = listOfValueChangeInfoAdapter,
            ),
        )
    }
    single { get<BackInTimeDatabase>().sessionInfoQueries }
    single { get<BackInTimeDatabase>().classInfoQueries }
    single { get<BackInTimeDatabase>().instanceQueries }
    single { get<BackInTimeDatabase>().eventLogQueries }
    single { get<BackInTimeDatabase>().methodCallInfoQueries }
}
