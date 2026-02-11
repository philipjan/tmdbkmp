package com.multiplatform.themoviedatabase.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.multiplatform.themoviedatabase.screens.splash.states.SplashState

@Composable
fun SplashScreen(
    state: SplashState,
    onDataLoaded: (() -> Unit)? = null
) {
    /*val randomM = Random.nextLong(from = 1, until = 10).times(1000)
    // TODO: simulate a 3 second delay loading
    LaunchedEffect(Unit) {
        delay(randomM)
        onDataLoaded?.invoke()
    }*/
    LaunchedEffect(state.movieData) {
        if (state.movieData != null && !state.isLoading && state.error == null) {
            onDataLoaded?.invoke()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        state.error?.let {
            Text("Error occured: ${it.message}")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }
    }
}