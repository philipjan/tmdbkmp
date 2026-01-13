package com.multiplatform.themoviedatabase.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.multiplatform.themoviedatabase.screens.landing.LandingScreen
import com.multiplatform.themoviedatabase.screens.splash.SplashScreen

enum class Screens(val title: String) {
    Splash("Splash"),
    Landing("Landing")
}


@Composable
fun TheMovieDatabaseApp(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screens.Landing.title,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = Screens.Splash.title ) {
                SplashScreen()
            }
            composable(route = Screens.Landing.title) {
                LandingScreen()
            }
        }
    }
}