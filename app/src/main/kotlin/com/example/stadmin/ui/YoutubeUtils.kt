package com.example.stadmin.ui

fun extractYoutubeVideoId(url: String): String? {
    val patterns = listOf(
        // https://youtu.be/VIDEO_ID
        // https://youtu.be/VIDEO_ID?si=...
        "youtu\\.be/([a-zA-Z0-9_-]{11})".toRegex(),
        // https://www.youtube.com/watch?v=VIDEO_ID
        // https://www.youtube.com/watch?v=VIDEO_ID&...
        "youtube\\.com/watch\\?v=([a-zA-Z0-9_-]{11})".toRegex(),
        // https://www.youtube.com/shorts/VIDEO_ID
        "youtube\\.com/shorts/([a-zA-Z0-9_-]{11})".toRegex(),
        // https://www.youtube.com/embed/VIDEO_ID
        "youtube\\.com/embed/([a-zA-Z0-9_-]{11})".toRegex()
    )

    patterns.forEach { pattern ->
        pattern.find(url)?.groupValues?.get(1)?.let { return it }
    }
    return null
}