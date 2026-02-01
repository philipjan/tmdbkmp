package com.multiplatform.themoviedatabase.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screens: NavKey {
    @Serializable
    data object Splash: Screens, NavKey
    @Serializable
    data object Landing: Screens, NavKey
}