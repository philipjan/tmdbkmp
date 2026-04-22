package com.multiplatform.data.model.mapper

import com.multiplatform.data.db.MovieEntity
import com.multiplatform.domain.entity.movie.Movie

// ─── MovieEntity → domain Movie ─────────────────────────────────────────────

internal fun MovieEntity.toDomain(): Movie =
    Movie(
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        title = title,
        video = video.toBoolean()
    ).apply {
        id = this@toDomain.id.toInt()
        adult = this@toDomain.adult.toBoolean()
        backdropPath = this@toDomain.backdropPath
        genreIds = this@toDomain.genreIds         // already decoded by GenreIdsAdapter
        originalLanguage = this@toDomain.originalLanguage
        overview = this@toDomain.overview
        popularity = this@toDomain.popularity
        posterPath = this@toDomain.posterPath
        voteAverage = this@toDomain.voteAverage
        voteCount = this@toDomain.voteCount.toInt()
    }

internal fun List<MovieEntity>.toDomain(): List<Movie> = map { it.toDomain() }

// ─── domain Movie → MovieEntity ─────────────────────────────────────────────

internal fun Movie.toEntity(page: Int): MovieEntity =
    MovieEntity(
        id = id?.toLong() ?: 0L,
        adult = adult.toLong(),
        backdropPath = backdropPath,
        genreIds = genreIds,                      // encoded by GenreIdsAdapter
        originalLanguage = originalLanguage,
        overview = overview ?: "",
        popularity = popularity ?: 0.0,
        posterPath = posterPath,
        voteAverage = voteAverage ?: 0.0,
        voteCount = voteCount?.toLong() ?: 0L,
        originalTitle = originalTitle,
        releaseDate = releaseDate,
        title = title,
        video = video.toLong(),
        page = page.toLong()
    )

internal fun List<Movie>.toEntity(page: Int): List<MovieEntity> = map { it.toEntity(page) }

// ─── Boolean ↔ Long helpers ──────────────────────────────────────────────────

private fun Boolean?.toLong(): Long = if (this == true) 1L else 0L
private fun Long.toBoolean(): Boolean = this == 1L

