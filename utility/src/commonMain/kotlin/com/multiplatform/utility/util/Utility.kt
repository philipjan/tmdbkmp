package com.multiplatform.utility.util

object Utility {
    fun String?.generateImageUrl(isDesktop: Boolean = false): String {
        // TODO: handle null values and display placeholder
        return if (isDesktop) {
            Endpoint.IMAGE_BASE_URL_DESKTOP + this
        } else {
            Endpoint.IMAGE_BASE_URL + this
        }
    }
}