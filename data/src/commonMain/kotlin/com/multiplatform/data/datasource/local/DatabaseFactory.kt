package com.multiplatform.data.datasource.local

import app.cash.sqldelight.db.SqlDriver
import com.multiplatform.data.db.AppDatabase
import com.multiplatform.data.datasource.local.adapter.MovieEntityAdapter

/** Platform-specific factory that produces the correct [SqlDriver] per target. */
expect class DatabaseDriverFactory {
    fun create(): SqlDriver
}

internal fun DatabaseDriverFactory.createAppDatabase(): AppDatabase =
    AppDatabase(
        driver = create(),
        MovieEntityAdapter = MovieEntityAdapter
    )


