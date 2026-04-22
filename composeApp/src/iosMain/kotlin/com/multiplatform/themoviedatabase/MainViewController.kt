package com.multiplatform.themoviedatabase

import androidx.compose.ui.window.ComposeUIViewController
import com.multiplatform.data.datasource.local.DatabaseDriverFactory

fun MainViewController() = ComposeUIViewController {
    App(driverFactory = DatabaseDriverFactory())
}
