package com.example.stadmin.ui.common

import android.util.Log

private const val DATE_TIME_PATTERN = "MMM d, yyyy"

fun formatDate(dateString: String): String {
    return try {
        val date = java.time.LocalDateTime.parse(
            dateString,
            java.time.format.DateTimeFormatter.ISO_DATE_TIME
        )
        date.format(java.time.format.DateTimeFormatter.ofPattern(DATE_TIME_PATTERN))
    } catch (e: Exception) {
        Log.e("DateFormatter", "Error: ${e.message}")
        dateString
    }
}