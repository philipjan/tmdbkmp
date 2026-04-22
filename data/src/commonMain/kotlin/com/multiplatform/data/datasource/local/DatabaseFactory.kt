package com.multiplatform.data.datasource.local

import app.cash.sqldelight.db.SqlDriver
import com.multiplatform.data.db.AppDatabase
import com.multiplatform.data.datasource.local.adapter.MovieEntityAdapter

/** Platform-specific factory that produces the correct [SqlDriver] per target. */
expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}

/**
 * Creates and initialises [AppDatabase].
 *
 * Schema lifecycle:
 * - Version 0 (fresh install) → [AppDatabase.Schema.create] is called to build all tables
 *   defined in `Movie.sq` (and any future `.sq` files).
 * - Version behind current → [AppDatabase.Schema.migrate] upgrades incrementally.
 * - Version current → no-op; DB is ready immediately.
 */
fun DatabaseDriverFactory.createAppDatabase(): AppDatabase {
    val driver = create()
    initialiseSchema(driver)
    return AppDatabase(
        driver = driver,
        MovieEntityAdapter = MovieEntityAdapter
    )
}

private fun initialiseSchema(driver: SqlDriver) {
    val schemaVersion = AppDatabase.Schema.version
    val currentVersion = driver.getVersion()

    when {
        currentVersion == 0L -> {
            AppDatabase.Schema.create(driver)
            driver.setVersion(schemaVersion)
        }
        currentVersion < schemaVersion -> {
            AppDatabase.Schema.migrate(driver, currentVersion, schemaVersion)
            driver.setVersion(schemaVersion)
        }
        // currentVersion == schemaVersion → nothing to do
    }
}

// ─── PRAGMA user_version helpers ────────────────────────────────────────────

private fun SqlDriver.getVersion(): Long =
    executeQuery(null, "PRAGMA user_version;", mapper = { cursor ->
        app.cash.sqldelight.db.QueryResult.Value(
            if (cursor.next().value) cursor.getLong(0) ?: 0L else 0L
        )
    }, parameters = 0).value

private fun SqlDriver.setVersion(version: Long) {
    execute(null, "PRAGMA user_version = $version;", parameters = 0)
}


