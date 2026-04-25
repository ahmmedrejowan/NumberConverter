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

    // Loading
    val isLoading: Boolean = false
)
