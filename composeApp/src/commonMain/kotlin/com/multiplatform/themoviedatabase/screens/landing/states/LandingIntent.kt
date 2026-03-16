package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.themoviedatabase.component.Intent

sealed interface LandingIntent: Intent {
    data class LoadInitialData(val initialMovieData: MovieData? = null): LandingIntent
    data class LoadSucceedingData(val newPage: Int?): LandingIntent
}