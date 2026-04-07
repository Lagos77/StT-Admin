package com.example.stadmin.screens.trace.domain

enum class TraceEra(val value: String) {
    OT("ot"),
    NT("nt"),
    MODERN("modern"),
    PROPHECY("prophecy"),
    UNKNOWN("unknown");

    companion object {
        fun fromValue(value: String?): TraceEra =
            entries.find { it.value == value } ?: UNKNOWN
    }
}