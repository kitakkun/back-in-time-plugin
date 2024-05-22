package com.github.kitakkun.backintime.debugger.data.di

import app.cash.sqldelight.db.SqlDriver
import com.github.kitakkun.backintime.debugger.data.BackInTimeDatabase
import com.github.kitakkun.backintime.debugger.data.driver.createSqlDriver
import com.github.kitakkun.backintime.debugger.data.repository.backInTimeDebugServiceEventAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfPropertyInfoAdapter
import com.github.kitakkun.backintime.debugger.data.repository.listOfStringAdapter
import com.github.kitakkun.backintime.debugger.database.ClassInfo
import com.github.kitakkun.backintime.debugger.database.EventLog
import com.github.kitakkun.backintime.debugger.database.Instance
import com.github.kitakkun.backintime.websocket.server.BackInTimeWebSocketServer
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module(includes = [SharedModule::class])
class DataModule {
    @Factory
    fun sqlDriver() = createSqlDriver()
}

@Module
@ComponentScan("com.github.kitakkun.backintime.debugger.data")
internal class SharedModule {
    @Single
    fun backInTimeWebSocketServer() = BackInTimeWebSocketServer()

    // sqldelight
    @Single
    fun backInTimeDatabase(driver: SqlDriver): BackInTimeDatabase {
        BackInTimeDatabase.Schema.create(driver)
        return BackInTimeDatabase(
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

    @Single
    fun sessionInfoQueries(database: BackInTimeDatabase) = database.sessionInfoQueries

    @Single
    fun classInfoQueries(database: BackInTimeDatabase) = database.classInfoQueries

    @Single
    fun instanceQueries(database: BackInTimeDatabase) = database.instanceQueries

    @Single
    fun eventLogQueries(database: BackInTimeDatabase) = database.eventLogQueries

    @Single
    fun methodCallInfoQueries(database: BackInTimeDatabase) = database.methodCallInfoQueries

    @Single
    fun valueChangeInfoQueries(database: BackInTimeDatabase) = database.valueChangeInfoQueries
}
