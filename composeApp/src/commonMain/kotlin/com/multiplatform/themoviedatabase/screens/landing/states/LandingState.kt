package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.themoviedatabase.component.State

data class LandingState(
    val movieData: MovieData? = null,
    val isLoading: Boolean = false,
    val error: Exception? = null
): State {
    companion object {
        fun initial(): LandingState {
            return LandingState(
                isLoading = true
            )
        }
    }
}