package com.example.stadmin.screens.trace.data.model

import com.example.stadmin.screens.trace.domain.model.Passage
import com.example.stadmin.screens.trace.domain.model.Source
import com.example.stadmin.screens.trace.domain.model.Trace
import com.example.stadmin.screens.trace.domain.model.Video
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TraceDto(
    @SerialName("id")
    val id: Int? = null,
    @SerialName("slug")
    val slug: String? = null,
    @SerialName("title")
    val title: String? = null,
    @SerialName("description")
    val description: String? = null,
    @SerialName("year")
    val year: Int? = null,
    @SerialName("is_nt")
    val isNt: Boolean? = null,
    @SerialName("image_url")
    val imageUrl: String? = null,
    @SerialName("hero_image_url")
    val heroImageUrl: String? = null,
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @SerialName("content")
    val content: List<String>? = null,
    @SerialName("videos")
    val videos: List<VideoDto>? = null,
    @SerialName("sources")
    val sources: List<SourceDto>? = null,
    @SerialName("passages")
    val passages: List<PassageDto>? = null,
    @SerialName("published")
    val published: Boolean? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("update_at")
    val updatedAt: String? = null
)
@Serializable
data class PassageDto(
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
@Serializable
data class VideoDto(
    @SerialName("url")
    val url: String? = null,
    @SerialName("label")
    val label: String? = null,
    @SerialName("platform")
    val platform: String? = null,
    @SerialName("videoId")
    val videoId: String? = null
)
@Serializable
data class SourceDto(
    @SerialName("url")
    val url: String? = null,
    @SerialName("label")
    val label: String? = null
)

fun TraceDto.toDomain(): Trace? {
    return Trace(
        id = id ?: return null,
        slug = slug ?: return null,
        title = title ?: return null,
        description = description,
        year = year,
        isNt = isNt ?: false,
        imageUrl = imageUrl,
        heroImageUrl = heroImageUrl,
        latitude = latitude,
        longitude = longitude,
        content = content,
        videos = videos?.mapNotNull { it.toDomain() },
        sources = sources?.mapNotNull { it.toDomain() },
        passages = passages?.mapNotNull { it.toDomain() },
        published = published ?: false,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun PassageDto.toDomain(): Passage? {
    return Passage(
        book = book ?: return null,
        chapter = chapter ?: return null,
        verseStart = verseStart ?: return null,
        verseEnd = verseEnd,
        text = text ?: return null,
        version = version ?: "NIV"
    )
}

fun VideoDto.toDomain(): Video? {
    return Video(
        url = url ?: return null,
        label = label ?: return null,
        platform = platform ?: return null,
        videoId = videoId ?: return null
    )
}

fun SourceDto.toDomain(): Source? {
    return Source(
        url = url ?: return null,
        label = label ?: return null
    )
}