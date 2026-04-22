package com.multiplatform.themoviedatabase

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.multiplatform.data.datasource.local.DatabaseDriverFactory

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TheMovieDatabase",
        alwaysOnTop = true
    ) {
        App(driverFactory = DatabaseDriverFactory())
    }
}