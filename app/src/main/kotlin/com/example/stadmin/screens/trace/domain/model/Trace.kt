package com.example.stadmin.screens.trace.domain.model

import com.example.stadmin.screens.trace.data.model.PassageDto
import com.example.stadmin.screens.trace.data.model.SourceDto
import com.example.stadmin.screens.trace.data.model.TraceDto
import com.example.stadmin.screens.trace.data.model.VideoDto

data class Trace(
    val id: Int?,
    val slug: String,
    val title: String,
    val description: String?,
    val year: Int?,
    val isNt: Boolean,
    val imageUrl: String?,
    val heroImageUrl: String?,
    val latitude: Double?,
    val longitude: Double?,
    val content: List<String>?,
    val videos: List<Video>?,
    val sources: List<Source>?,
    val passages: List<Passage>?,
    val published: Boolean,
    val createdAt: String?,
    val updatedAt: String?
) {
    val isComplete: Boolean
        get() = !title.isNullOrBlank() &&
                !description.isNullOrBlank() &&
                !imageUrl.isNullOrBlank() &&
                !heroImageUrl.isNullOrBlank() &&
                latitude != null &&
                longitude != null &&
                year != null &&
                !content.isNullOrEmpty() &&
                !passages.isNullOrEmpty() &&
                !videos.isNullOrEmpty() &&
                !sources.isNullOrEmpty()
}

data class Passage(
    val book: String,
    val chapter: Int?,
    val verseStart: Int?,
    val verseEnd: Int?,
    val text: String,
    val version: String?
)

data class Video(
    val url: String,
    val label: String,
    val platform: String,
    val videoId: String
)

data class Source(
    val url: String,
    val label: String
)

fun Trace.toDto(): TraceDto = TraceDto(
    id = id,
    slug = slug,
    title = title,
    description = description,
    year = year,
    isNt = isNt,
    imageUrl = imageUrl,
    heroImageUrl = heroImageUrl,
    latitude = latitude,
    longitude = longitude,
    content = content,
    videos = videos?.map { it.toDto() },
    sources = sources?.map { it.toDto() },
    passages = passages?.map { it.toDto() },
    published = published,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Passage.toDto(): PassageDto {
    return PassageDto(
        book = book,
        chapter = chapter,
        verseStart = verseStart,
        verseEnd = verseEnd,
        text = text,
        version = version
    )
}

fun Video.toDto(): VideoDto {
    return VideoDto(
        url = url,
        label = label,
        platform = platform,
        videoId = videoId
    )
}

fun Source.toDto(): SourceDto {
    return SourceDto(
        url = url,
        label = label
    )
}