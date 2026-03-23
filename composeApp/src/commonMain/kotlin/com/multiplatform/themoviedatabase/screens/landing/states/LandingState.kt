package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.themoviedatabase.component.State

data class LandingState(
    val movieData: MovieData? = null,
    val isLoading: Boolean = false,
    val isPagingLoading: Boolean = false,
    val error: Exception? = null
): State {
    companion object {
        fun initial(movieData: MovieData? = null): LandingState {
            return LandingState(
                movieData = movieData,
                isLoading = movieData == null
            )
        }
    }
}