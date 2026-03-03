package com.multiplatform.themoviedatabase.screens.landing

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.MviViewModel
import com.multiplatform.themoviedatabase.screens.landing.states.LandingIntent
import com.multiplatform.themoviedatabase.screens.landing.states.LandingReducer
import com.multiplatform.themoviedatabase.screens.landing.states.LandingState

class LandingViewModel(
    movieRepository: MovieRepository<MovieData>
): MviViewModel<LandingState, LandingIntent>(
    reducer = LandingReducer(movieRepository = movieRepository),
    initialState = LandingState.initial()
) {

    init {

    }
    
}