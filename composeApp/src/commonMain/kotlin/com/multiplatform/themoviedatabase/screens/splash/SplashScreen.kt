package com.multiplatform.themoviedatabase.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun SplashScreen(
    onDataLoaded: (() -> Unit)? = null
) {
    val randomM = Random.nextLong(from = 1, until = 10).times(1000)
    // TODO: simulate a 3 second delay loading
    LaunchedEffect(Unit) {
        delay(randomM)
        onDataLoaded?.invoke()
    }

    Box(
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}