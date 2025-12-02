package com.rejowan.numberconverter.domain.usecase.settings

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager

class UpdateSettingUseCase(
    private val preferencesManager: PreferencesManager
) {
    suspend fun updateTheme(theme: String) {
        preferencesManager.setTheme(theme)
    }

    suspend fun updateDynamicColors(enabled: Boolean) {
        preferencesManager.setDynamicColors(enabled)
    }

    suspend fun updateFontSize(size: String) {
        preferencesManager.setFontSize(size)
    }

    suspend fun updateDecimalPlaces(places: Int) {
        preferencesManager.setDecimalPlaces(places)
    }

    suspend fun updateAutoSaveHistory(enabled: Boolean) {
        preferencesManager.setAutoSaveHistory(enabled)
    }

    suspend fun updateShowExplanations(enabled: Boolean) {
        preferencesManager.setShowExplanations(enabled)
    }

    suspend fun updateInputValidation(mode: String) {
        preferencesManager.setInputValidation(mode)
    }

    suspend fun updateAutoAdvanceLessons(enabled: Boolean) {
        preferencesManager.setAutoAdvanceLessons(enabled)
    }

    suspend fun updateDailyReminders(enabled: Boolean) {
        preferencesManager.setDailyReminders(enabled)
    }

    suspend fun resetAllSettings() {
        preferencesManager.resetAllPreferences()
    }
}
