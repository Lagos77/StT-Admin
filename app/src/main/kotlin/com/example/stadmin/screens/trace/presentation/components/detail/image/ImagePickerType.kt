package com.example.stadmin.screens.trace.presentation.components.detail.image

enum class ImagePickerType(
    val label: String,
    val aspectRatioX: Float,
    val aspectRatioY: Float,
    val maxWidth: Int,
    val maxHeight: Int,
) {
    CARD(
        label = "Card Image",
        aspectRatioX = 800f,
        aspectRatioY = 360f,
        maxWidth = 800,
        maxHeight = 360
    ),
    HERO(
        label = "Hero Image",
        aspectRatioX = 1440f,
        aspectRatioY = 420f,
        maxWidth = 1440,
        maxHeight = 420
    )
}