package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.Reducer
import com.multiplatform.utility.util.Endpoint

class LandingReducer(
    private val movieRepository: MovieRepository<MovieData>
): Reducer<LandingState, LandingIntent> {
    override suspend fun reduce(
        state: LandingState,
        intent: LandingIntent
    ): LandingState {
        return when(intent) {
            LandingIntent.LoadInitialData -> {
                loadInitialData(state)
            }

            is LandingIntent.LoadSucceedingData -> TODO()
        }
    }

    private suspend fun loadInitialData(state: LandingState): LandingState {
        return try {
            state.copy(isLoading = true)
            val result = movieRepository.getMediaLists(
                endpoint = Endpoint.ENDPOINT_POPULAR,
                // initial data, so first page
                page = 1
            )
            state.copy(movieData = result, isLoading = false)
        } catch (e: Exception) {
            state.copy(isLoading = false, error = e)
        }
    }
}
