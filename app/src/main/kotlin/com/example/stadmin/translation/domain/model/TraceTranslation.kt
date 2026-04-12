package com.example.stadmin.translation.domain.model

import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.translation.data.model.TraceTranslationDto
import com.example.stadmin.translation.presentation.TranslationLanguage
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
        passages = passages?.map { dto ->
            Passage(
                book = dto.book ?: "",
                chapter = dto.chapter,
                verseStart = dto.verseStart,
                verseEnd = dto.verseEnd,
                text = dto.text ?: "",
                version = dto.version ?: ""
            )
        },
        videos = videos,
    )
}