package com.multiplatform.themoviedatabase.screens.splash

import com.multiplatform.domain.entity.movie.MovieData
import com.multiplatform.domain.repository.MovieRepository
import com.multiplatform.themoviedatabase.component.MviViewModel
import com.multiplatform.themoviedatabase.screens.splash.states.SplashIntent
import com.multiplatform.themoviedatabase.screens.splash.states.SplashReducer
import com.multiplatform.themoviedatabase.screens.splash.states.SplashState
import kotlinx.coroutines.CoroutineScope

class SplashViewModel(
    movieRepository: MovieRepository<MovieData>,
    initialState: SplashState = SplashState.initial(),
    coroutineScope: CoroutineScope? = null,
): MviViewModel<SplashState, SplashIntent>(
    reducer = SplashReducer(movieRepository = movieRepository),
    initialState = initialState,
    coroutineScope = coroutineScope,
) {

    init {
        loadData()
    }

    fun loadData() {
        dispatch(SplashIntent.LoadInitialData)
    }
}