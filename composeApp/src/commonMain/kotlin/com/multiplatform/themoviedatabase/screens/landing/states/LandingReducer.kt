package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.Reducer
import com.multiplatform.utility.util.Constants
import com.multiplatform.utility.util.Endpoint

class LandingReducer(
    private val movieRepository: MovieRepository<MovieData>
): Reducer<LandingState, LandingIntent> {

    override suspend fun reduce(
        state: LandingState,
        intent: LandingIntent
    ): LandingState {
        return when(intent) {
            is LandingIntent.LoadInitialData -> {
                loadInitialData(initialData = intent.initialMovieData, state = state)
            }

            is LandingIntent.LoadSucceedingData -> {
                loadSucceedingData(state)
            }
        }
    }

    private suspend fun loadSucceedingData(state: LandingState): LandingState {
        return try {
            state.copy(isPagingLoading = true)
            val result = movieRepository.getMediaLists(
                endpoint = Endpoint.ENDPOINT_POPULAR,
                page = if (state.movieData?.movies.isNullOrEmpty()) null else state.movieData.page
            )
            state.copy(movieData = result, isPagingLoading = false)
        } catch (e: Exception) {
            state.copy(isPagingLoading = false, error = e)
        }
    }

    private suspend fun loadInitialData(initialData: MovieData?, state: LandingState): LandingState {
        return try {
            state.copy(isLoading = true)
            if (initialData != null) {
                state.copy(movieData = initialData, isLoading = false)
            } else {
                val result = movieRepository.getMediaLists(
                    endpoint = Endpoint.ENDPOINT_POPULAR,
                    // initial data, so first page
                    page = Constants.INITIAL_PAGE
                )
                state.copy(movieData = result, isLoading = false)
            }
        } catch (e: Exception) {
            state.copy(isLoading = false, error = e)
        }
    }
}
