package com.multiplatform.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json


object TmdbHttpClient {

    val client = HttpClient(CIO) {
        install(Auth) {
            // authentication here
        }
        install(ContentNegotiation) {
            // content negotionation config here
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                }
            )
        }

        engine {
            endpoint {
                connectTimeout = 15000
                connectAttempts = 5
            }
        }
        defaultRequest {
            url("https://api.themoviedb.org/4/")
            headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.contentType)
        }
    }
}