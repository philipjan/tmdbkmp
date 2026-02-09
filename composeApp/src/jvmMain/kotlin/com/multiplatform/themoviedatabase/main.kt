package com.multiplatform.themoviedatabase

import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.multiplatform.themoviedatabase.component.LoaderHotReloadContainer
import com.multiplatform.themoviedatabase.component.Media

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "TheMovieDatabase",
        alwaysOnTop = true
    ) {
//        App()
//        Media(
//            sourceUrl = "https://picsum.photos/200/300",
//            modifier = Modifier
//        )
        LoaderHotReloadContainer()
    }
}