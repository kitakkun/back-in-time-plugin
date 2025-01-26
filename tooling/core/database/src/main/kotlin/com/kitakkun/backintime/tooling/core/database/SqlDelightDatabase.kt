package com.kitakkun.backintime.tooling.core.database

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.kitakkun.backintime.tooling.model.EventEntity
import kotlinx.serialization.json.Json

private val eventEntityAdapter = object : ColumnAdapter<EventEntity, String> {
    override fun encode(value: EventEntity): String {
        return Json.encodeToString(value)
    }

    override fun decode(databaseValue: String): EventEntity {
        return Json.decodeFromString(databaseValue)
    }
}

fun createDatabase(url: String = JdbcSqliteDriver.IN_MEMORY): Database {
    // Fix no suitable driver found for jdbc:sqlite:
    // FYI: https://stackoverflow.com/questions/16725377/unable-to-connect-to-database-no-suitable-driver-found
    Class.forName("org.sqlite.JDBC")

    val driver = JdbcSqliteDriver(url)
    Database.Schema.create(driver)
    return Database(
        driver = driver,
        eventAdapter = Event.Adapter(eventAdapter = eventEntityAdapter)
    )
}
