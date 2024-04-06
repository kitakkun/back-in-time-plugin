package com.github.kitakkun.backintime.debugger.repository

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual fun createTestSqlDriver(): SqlDriver {
    return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
}
