package com.example.stadmin.translation.presentation

enum class TranslationLanguage(val code: String) {
    ES("es"),
    PT("pt"),
    SV("sv");

    companion object {
        fun fromCode(code: String): TranslationLanguage? =
            entries.find { it.code == code }
    }
}