package com.multiplatform.utility.util

object Utility {
    fun String?.generateImageUrl(): String {
        // TODO: handle null values and display placeholder
        return Endpoint.IMAGE_BASE_URL.plus(this).trim()
    }
}