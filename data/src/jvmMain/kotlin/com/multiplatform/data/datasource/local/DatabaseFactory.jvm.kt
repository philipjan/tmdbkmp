package com.multiplatform.data.datasource.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DatabaseDriverFactory(private val dbPath: String = JdbcSqliteDriver.IN_MEMORY) {
    actual fun create(): SqlDriver = JdbcSqliteDriver(dbPath)
}

