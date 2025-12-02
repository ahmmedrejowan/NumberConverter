package com.rejowan.numberconverter.presentation.settings.state

data class SettingsUiState(
    // Appearance
    val theme: String = "system",
    val dynamicColors: Boolean = true,
    val fontSize: String = "medium",

    // Converter
    val decimalPlaces: Int = 15,
    val autoSaveHistory: Boolean = true,
    val showExplanations: Boolean = true,
    val inputValidation: String = "strict",

    // Learning
    val autoAdvanceLessons: Boolean = false,
    val dailyReminders: Boolean = false,

    // UI state
    val isLoading: Boolean = false,
    val showThemeDialog: Boolean = false,
    val showFontSizeDialog: Boolean = false,
    val showDecimalPlacesDialog: Boolean = false
)
