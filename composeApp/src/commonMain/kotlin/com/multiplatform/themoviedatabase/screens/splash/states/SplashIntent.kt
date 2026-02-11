package com.multiplatform.themoviedatabase.screens.splash.states

import com.multiplatform.themoviedatabase.component.Intent

sealed interface SplashIntent: Intent {
    data object LoadInitialData: SplashIntent
}