package com.example.stadmin.translation.data.model

import com.example.stadmin.translation.domain.model.TraceTranslation
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
data class TraceTranslationDto(
    @SerialName("id")
    val id: String? = null,
    @SerialName("trace_slug")
    val traceSlug: String? = null,
    @SerialName("language")
    val language: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("content")
    val content: JsonArray? = null,
    @SerialName("passages")
    val passages: List<TranslationPassageDto>? = null,
    @SerialName("videos")
    val videos: List<String>? = null,
)

@Serializable
data class TranslationPassageDto(
    @SerialName("book")
    val book: String? = null,
    @SerialName("chapter")
    val chapter: Int? = null,
    @SerialName("verse_start")
    val verseStart: Int? = null,
    @SerialName("verse_end")
    val verseEnd: Int? = null,
    @SerialName("text")
    val text: String? = null,
    @SerialName("version")
    val version: String? = null
)

fun TraceTranslation.toDto() = TraceTranslationDto(
    id = id,
    traceSlug = traceSlug,
    language = language.code,
    title = title,
    description = description,
    content = content?.let { Json.encodeToJsonElement(it).jsonArray },
    passages = passages?.map { passage ->
        TranslationPassageDto(
            book = passage.book,
            chapter = passage.chapter,
            verseStart = passage.verseStart,
            verseEnd = passage.verseEnd,
            text = passage.text,
            version = passage.version
        )
    },
    videos = videos,
)