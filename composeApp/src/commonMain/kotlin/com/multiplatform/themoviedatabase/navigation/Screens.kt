package com.multiplatform.themoviedatabase.navigation

import androidx.navigation3.runtime.NavKey
import com.multiplatform.domain.entity.movie.Movie
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screens: NavKey {
    @Serializable
    data class MovieDetails(val movie: Movie): Screens
    @Serializable
    data object Splash: Screens
    @Serializable
    data object Landing: Screens
}