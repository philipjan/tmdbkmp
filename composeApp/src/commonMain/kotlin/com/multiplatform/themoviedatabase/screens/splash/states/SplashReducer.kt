package com.multiplatform.themoviedatabase.screens.splash.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.Reducer
import com.multiplatform.utility.util.Endpoint

class SplashReducer(
    private val movieRepository: MovieRepository<MovieData>
): Reducer<SplashState, SplashIntent> {
    override suspend fun reduce(
        state: SplashState,
        intent: SplashIntent
    ): SplashState {
        return when(intent) {
            SplashIntent.LoadInitialData -> {
                loadInitialData(state)
            }

            is SplashIntent.LoadSucceedingData -> TODO()
        }
    }

    private suspend fun loadInitialData(state: SplashState): SplashState {
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