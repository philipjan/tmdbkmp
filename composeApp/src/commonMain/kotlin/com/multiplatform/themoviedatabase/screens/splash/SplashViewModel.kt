package com.multiplatform.themoviedatabase.screens.splash

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.MviViewModel
import com.multiplatform.themoviedatabase.screens.splash.states.SplashIntent
import com.multiplatform.themoviedatabase.screens.splash.states.SplashReducer
import com.multiplatform.themoviedatabase.screens.splash.states.SplashState

class SplashViewModel(
    movieRepository: MovieRepository<MovieData>,
    initialState: SplashState = SplashState.initial()
): MviViewModel<SplashState, SplashIntent>(
    reducer = SplashReducer(movieRepository = movieRepository),
    initialState = initialState
) {

    init {
        loadData()
    }

    fun loadData() {
        dispatch(SplashIntent.LoadInitialData)
    }
}