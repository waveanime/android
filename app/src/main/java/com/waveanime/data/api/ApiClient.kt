package com.waveanime.data.api

import com.waveanime.data.model.HomeResponse
import com.waveanime.data.model.SearchAnimeItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ApiClient {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
            })
        }
    }

    suspend fun getHome(): HomeResponse = client.get("https://waveanime.fr/api/home").body()

    suspend fun searchSeries(query: String): List<SearchAnimeItem> {
        return client.get("https://waveanime.fr/api/series") {
            parameter("query", query)
            parameter("order", "latest")
            parameter("limit", 10)
        }.body()
    }
}