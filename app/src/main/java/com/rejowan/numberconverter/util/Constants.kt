package com.rejowan.numberconverter.util

object Constants {
    // Database
    const val DATABASE_NAME = "number_converter_db"
    const val DATABASE_VERSION = 1

    // DataStore
    const val PREFERENCES_NAME = "number_converter_prefs"

    // Converter
    const val DEFAULT_DECIMAL_PLACES = 10
    const val MAX_DECIMAL_PLACES = 30
    const val MIN_DECIMAL_PLACES = 1

    // History
    const val DEFAULT_HISTORY_LIMIT = 50
    const val RECENT_HISTORY_LIMIT = 10

    // Practice
    const val DEFAULT_PRACTICE_PROBLEMS = 10
    const val TIMED_QUIZ_DURATION_SECONDS = 120
    const val STREAK_BONUS_MULTIPLIER = 0.1f

    // Lessons
    const val TOTAL_LESSONS = 18

    // UI
    const val ANIMATION_DURATION_MS = 300
    const val DEBOUNCE_DELAY_MS = 300L
}
