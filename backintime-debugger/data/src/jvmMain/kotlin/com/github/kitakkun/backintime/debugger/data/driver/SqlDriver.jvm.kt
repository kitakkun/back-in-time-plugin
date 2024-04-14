package com.github.kitakkun.backintime.debugger.data.driver

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual fun createSqlDriver(): SqlDriver {
    return JdbcSqliteDriver("jdbc:sqlite:test.db")
}
