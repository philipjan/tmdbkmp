package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.Movie
import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.themoviedatabase.component.Intent

sealed interface LandingIntent: Intent {
    // Initial load pipeline
    data class InitialLoadingChanged(val isLoading: Boolean): LandingIntent
    data class InitialDataLoaded(val movieData: MovieData): LandingIntent

    // Paging pipeline (driven by PaginationManager)
    data class PagingLoadingChanged(val isLoading: Boolean): LandingIntent
    data class PagingDataAppended(
        val page: Int?,
        val totalPages: Int?,
        val movies: List<Movie>
    ): LandingIntent

    // Legacy intents retained for compatibility (no-ops in reducer)
    data class LoadInitialData(val initialMovieData: MovieData? = null): LandingIntent
    data class LoadSucceedingData(val newPage: Int?): LandingIntent

    data class Error(val error: Exception?): LandingIntent
}