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
        ) { values: Array<*> ->
            AppSettings(
                theme = values[0] as String,
                dynamicColors = values[1] as Boolean,
                fontSize = values[2] as String,
                decimalPlaces = values[3] as Int,
                autoSaveHistory = values[4] as Boolean,
                showExplanations = values[5] as Boolean,
                inputValidation = values[6] as String,
                autoAdvanceLessons = values[7] as Boolean,
                dailyReminders = values[8] as Boolean
            )
        }
    }
}
