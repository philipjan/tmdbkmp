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

interface TmdbHttpClient {
    fun getClient(): HttpClient
}

class ITmdbHttpClient(val tkn: String) : TmdbHttpClient {

    private var env: Environment = Environment.Dev
    private var urlValue: String = when(env) {
        Environment.Dev -> "https://api.themoviedb.org/3/"
        else -> "https://api.themoviedb.org/3/"
    }

    private lateinit var client: HttpClient

    override fun getClient(): HttpClient {
        if (!::client.isInitialized) {
            client = createClient(tkn)
        }
        return client
    }

    private fun createClient(token: String): HttpClient {
        return HttpClient(CIO) {
            install(Auth) {
                // authentication here
                bearer {
                    loadTokens {
                        BearerTokens(
                            accessToken = token,
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
                url(urlValue.trim())
                headers.appendIfNameAbsent(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                headers.appendIfNameAbsent(HttpHeaders.Accept, ContentType.Application.Json.toString())
            }
        }
    }
}