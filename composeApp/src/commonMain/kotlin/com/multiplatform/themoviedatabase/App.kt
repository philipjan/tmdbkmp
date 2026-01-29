package com.multiplatform.themoviedatabase

import androidx.compose.runtime.Composable
import com.multiplatform.themoviedatabase.navigation.TheMovieDatabaseApp
import com.multiplatform.themoviedatabase.ui.theme.TmdbTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    TmdbTheme {
        TheMovieDatabaseApp()
    }
}