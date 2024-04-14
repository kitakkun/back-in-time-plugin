package com.github.kitakkun.backintime.debugger.repository

import app.cash.sqldelight.db.SqlDriver

expect fun createTestSqlDriver(): SqlDriver
