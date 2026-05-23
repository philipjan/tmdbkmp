package com.multiplatform.themoviedatabase


interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
