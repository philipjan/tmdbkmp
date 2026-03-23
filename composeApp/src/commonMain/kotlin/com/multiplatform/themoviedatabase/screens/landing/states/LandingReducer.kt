package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.themoviedatabase.component.Reducer

class LandingReducer: Reducer<LandingState, LandingIntent> {

    override suspend fun reduce(
        state: LandingState,
        intent: LandingIntent
    ): LandingState {
        return when(intent) {
            is LandingIntent.InitialLoadingChanged -> state.copy(isLoading = intent.isLoading, error = null)
            is LandingIntent.InitialDataLoaded -> state.copy(movieData = intent.movieData, isLoading = false, error = null)

            is LandingIntent.PagingLoadingChanged -> state.copy(isPagingLoading = intent.isLoading, error = null)
            is LandingIntent.PagingDataAppended -> state.appendMovies(intent)

            is LandingIntent.Error -> state.copy(isLoading = false, isPagingLoading = false, error = intent.error)

            // Legacy intents are treated as no-ops
            is LandingIntent.LoadInitialData -> state
            is LandingIntent.LoadSucceedingData -> state
        }
    }

    private fun LandingState.appendMovies(intent: LandingIntent.PagingDataAppended): LandingState {
        val updatedMovies = movieData?.movies.orEmpty() + intent.movies
        val updatedMovieData = MovieData(
            page = intent.page ?: movieData?.page,
            totalPages = intent.totalPages ?: movieData?.totalPages,
            movies = updatedMovies
        )
        return copy(movieData = updatedMovieData, isPagingLoading = false, error = null)
    }
}
