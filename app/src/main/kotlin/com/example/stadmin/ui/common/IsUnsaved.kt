package com.example.stadmin.ui.common

fun isUnsaved(translated: String, base: String?): Boolean {
    return !base.isNullOrBlank() && translated.isBlank()
}