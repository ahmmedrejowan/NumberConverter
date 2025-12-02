package com.rejowan.numberconverter.domain.usecase.settings

import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

data class AppSettings(
    val theme: String,
    val dynamicColors: Boolean,
    val fontSize: String,
    val decimalPlaces: Int,
    val autoSaveHistory: Boolean,
    val showExplanations: Boolean,
    val inputValidation: String,
    val autoAdvanceLessons: Boolean,
    val dailyReminders: Boolean
)

class GetSettingsUseCase(
    private val preferencesManager: PreferencesManager
) {
    operator fun invoke(): Flow<AppSettings> {
        return combine(
            preferencesManager.theme,
            preferencesManager.dynamicColors,
            preferencesManager.fontSize,
            preferencesManager.decimalPlaces,
            preferencesManager.autoSaveHistory,
            preferencesManager.showExplanations,
            preferencesManager.inputValidation,
            preferencesManager.autoAdvanceLessons,
            preferencesManager.dailyReminders
        ) { theme, dynamicColors, fontSize, decimalPlaces, autoSaveHistory,
            showExplanations, inputValidation, autoAdvanceLessons, dailyReminders ->
            AppSettings(
                theme = theme,
                dynamicColors = dynamicColors,
                fontSize = fontSize,
                decimalPlaces = decimalPlaces,
                autoSaveHistory = autoSaveHistory,
                showExplanations = showExplanations,
                inputValidation = inputValidation,
                autoAdvanceLessons = autoAdvanceLessons,
                dailyReminders = dailyReminders
            )
        }
    }
}
