package com.example.stadmin.core.deepl

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val BASE_URL = "https://api-free.deepl.com/v2/translate"

class DeepDataSource(private val apiKey: String) {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun translate(text: String, targetLanguage: String, sourceLanguage: String): String? {
        val response = client.post(BASE_URL) {
            contentType(ContentType.Application.Json)
            header("Authorization", "DeepL-Auth-Key $apiKey")
            setBody(
                DeepLRequest(
                    text = listOf(text),
                    targetLanguage = targetLanguage,
                    sourceLanguage = sourceLanguage
                )
            )
        }
        return if (response.status.isSuccess()) {
            response.body<DeepLResponse>().translations.firstOrNull()?.text
        } else {
            null
        }
    }

    fun closeClient() = client.close()

    @Serializable
    private data class DeepLRequest(
        val text: List<String>,
        @SerialName("target_lang") val targetLanguage: String,
        @SerialName("source_lang") val sourceLanguage: String?
    )
    @Serializable
    private data class DeepLResponse(
        val translations: List<Translation>
    ) {
        @Serializable
        data class Translation(val text: String)
    }
}