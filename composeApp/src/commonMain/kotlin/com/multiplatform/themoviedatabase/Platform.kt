package com.multiplatform.themoviedatabase

import androidx.compose.runtime.Composable


interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
