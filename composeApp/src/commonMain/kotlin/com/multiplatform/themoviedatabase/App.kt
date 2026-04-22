package com.multiplatform.themoviedatabase

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.multiplatform.data.datasource.local.DatabaseDriverFactory
import com.multiplatform.themoviedatabase.navigation.TheMovieDatabaseApp
import com.multiplatform.themoviedatabase.ui.theme.TmdbTheme

@Composable
//@Preview() // temporary commented out
fun App(driverFactory: DatabaseDriverFactory) {
    TmdbTheme {
        TheMovieDatabaseApp(driverFactory = driverFactory)
    }
}