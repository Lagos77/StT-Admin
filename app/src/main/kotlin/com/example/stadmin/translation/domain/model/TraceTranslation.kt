package com.example.stadmin.translation.domain.model

import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.translation.data.model.TraceTranslationDto
import com.example.stadmin.translation.presentation.TranslationLanguage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonPrimitive

data class TraceTranslation(
    val id: String?,
    val traceSlug: String,
    val language: TranslationLanguage,
    val title: String?,
    val description: String?,
    val content: List<String>?,
    val passages: List<Passage>?,
    val videos: List<String>?,
)

fun TraceTranslationDto.toDomain(): TraceTranslation? {
    return TraceTranslation(
        id = id,
        traceSlug = traceSlug ?: "",
        language = TranslationLanguage.fromCode(language ?: "") ?: return null,
        title = title,
        description = description,
        content = content?.map { it.jsonPrimitive.content },
        passages = passages?.let { Json.decodeFromJsonElement(it) },
        videos = videos,
    )
}