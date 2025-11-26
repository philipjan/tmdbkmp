package com.multiplatform.themoviedatabase

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.multiplatform.themoviedatabase.navigation.TheMovieDatabaseApp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        TheMovieDatabaseApp()
    }
}