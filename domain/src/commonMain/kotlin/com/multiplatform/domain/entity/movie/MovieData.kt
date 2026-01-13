package com.multiplatform.domain.entity.movie

data class MovieData(
    val page: Int?,
    val totalPages: Int?,
    val movies: List<Movie>
)
