package com.multiplatform.themoviedatabase.screens.landing.states

import com.multiplatform.themoviedatabase.component.Intent

sealed interface LandingIntent: Intent {
    data object LoadInitialData: LandingIntent
    data class LoadSucceedingData(val newPage: Int): LandingIntent
}