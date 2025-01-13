package com.kitakkun.backintime.tooling.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kitakkun.backintime.tooling.model.InstanceEventData
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val instanceEventAdapter = object : ColumnAdapter<InstanceEventData, String> {
    override fun encode(value: InstanceEventData): String {
        return Json.encodeToString(value)
    }

    override fun decode(databaseValue: String): InstanceEventData {
        return Json.decodeFromString(databaseValue)
    }
}

fun createDatabase(url: String = JdbcSqliteDriver.IN_MEMORY): Database {
    val driver = JdbcSqliteDriver(url)
    Database.Schema.create(driver)
    return Database(
        driver = driver,
        instanceEventAdapter = InstanceEvent.Adapter(eventAdapter = instanceEventAdapter),
    )
}
