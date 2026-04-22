package com.multiplatform.data.datasource.local.adapter

import app.cash.sqldelight.ColumnAdapter

/**
 * Converts [List<Int?>?] (domain genreIds) to a comma-separated TEXT column and back.
 * Empty/null lists are stored as an empty string.
 */
internal object GenreIdsAdapter : ColumnAdapter<List<Int>, String> {

    private const val DELIMITER = ","

    override fun decode(databaseValue: String): List<Int> =
        if (databaseValue.isBlank()) emptyList()
        else databaseValue.split(DELIMITER).map { it.trimEnd().toIntOrNull() ?: -1 }

    override fun encode(value: List<Int>): String =
        value.joinToString(DELIMITER) { it.toString() }
}

