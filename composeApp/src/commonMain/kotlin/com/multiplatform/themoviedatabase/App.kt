package com.multiplatform.themoviedatabase

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.multiplatform.themoviedatabase.navigation.TheMovieDatabaseApp
import com.multiplatform.themoviedatabase.ui.theme.TmdbTheme

@Composable
@Preview(showSystemUi = true)
fun App() {
    TmdbTheme {
        TheMovieDatabaseApp()
    }
}