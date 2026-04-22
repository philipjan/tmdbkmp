package com.multiplatform.data.datasource.local.adapter

import com.multiplatform.data.db.MovieEntity

/**
 * Pre-built [MovieEntity.Adapter] that wires all custom [app.cash.sqldelight.ColumnAdapter]s
 * required by the `MovieEntity` table.
 *
 * Consumed by [com.multiplatform.data.datasource.local.DatabaseDriverFactory.createAppDatabase].
 */
internal val MovieEntityAdapter: MovieEntity.Adapter
    get() = MovieEntity.Adapter(
        genreIdsAdapter = GenreIdsAdapter
    )

