package com.multiplatform.data.datasource.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.cio.endpoint
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import kotlinx.serialization.json.Json

sealed class Environment {
    object Prod: Environment()
    object Dev: Environment()
}

object TmdbHttpClient {

    private var env: Environment = Environment.Dev
    private var url: String = when(env) {
        Environment.Dev -> "https://api.themoviedb.org/3/"
        else -> "https://api.themoviedb.org/3/"
    }

    val client = HttpClient(CIO) {
        install(Auth) {
            // authentication here
            bearer {
                loadTokens {
                    BearerTokens(
                        accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIwMjgxYWY5Y2VlZThlMTlhYjdhOTliN2YwNTZhMmQ4NiIsIm5iZiI6MTQ5MjQyNjI0Ny4zODUsInN1YiI6IjU4ZjQ5ZTAzOTI1MTQxM2RhNzAyMTdmZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.q0n-3DNnS9fXu1cs_87GIKj7YSKYSInd9Pe5puxpxWI",
                        refreshToken = null
                    )
                }
            }
        }

        // TODO: not working, fix once available
        install(Logging) {
            level = LogLevel.ALL
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
                connectTimeout = 10000
                connectAttempts = 5
            }
        }
        defaultRequest {
            url(TmdbHttpClient.url.trim())
            headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            headers.appendIfNameAbsent(HttpHeaders.Accept, ContentType.Application.Json.toString())
        }
    }
}