package com.multiplatform.themoviedatabase.screens.splash.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.themoviedatabase.component.State

data class SplashState(
    val movieData: MovieData? = null,
    val isLoading: Boolean = false,
    val error: Exception? = null
): State {
    companion object {
        fun initial(): SplashState {
            return SplashState(
                isLoading = true
            )
        }
    }
}