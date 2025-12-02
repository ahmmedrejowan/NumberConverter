package com.rejowan.numberconverter.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Manages app preferences using DataStore.
 */
class PreferencesManager(private val context: Context) {

    companion object {
        // Converter preferences
        private val KEY_DECIMAL_PLACES = intPreferencesKey("decimal_places")
        private val KEY_AUTO_SAVE_HISTORY = booleanPreferencesKey("auto_save_history")
        private val KEY_SHOW_EXPLANATIONS = booleanPreferencesKey("show_explanations")
        private val KEY_INPUT_VALIDATION = stringPreferencesKey("input_validation")

        // Appearance preferences
        private val KEY_THEME = stringPreferencesKey("theme")
        private val KEY_DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        private val KEY_FONT_SIZE = stringPreferencesKey("font_size")

        // Learning preferences
        private val KEY_AUTO_ADVANCE_LESSONS = booleanPreferencesKey("auto_advance_lessons")
        private val KEY_DAILY_REMINDERS = booleanPreferencesKey("daily_reminders")

        // Default values
        const val DEFAULT_DECIMAL_PLACES = 15
        const val DEFAULT_THEME = "system"
        const val DEFAULT_FONT_SIZE = "medium"
        const val DEFAULT_INPUT_VALIDATION = "strict"
    }

    // Converter preferences
    val decimalPlaces: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_DECIMAL_PLACES] ?: DEFAULT_DECIMAL_PLACES
    }

    suspend fun setDecimalPlaces(places: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DECIMAL_PLACES] = places
        }
    }

    val autoSaveHistory: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTO_SAVE_HISTORY] ?: true
    }

    suspend fun setAutoSaveHistory(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_SAVE_HISTORY] = enabled
        }
    }

    val showExplanations: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SHOW_EXPLANATIONS] ?: true
    }

    suspend fun setShowExplanations(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SHOW_EXPLANATIONS] = enabled
        }
    }

    val inputValidation: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_INPUT_VALIDATION] ?: DEFAULT_INPUT_VALIDATION
    }

    suspend fun setInputValidation(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_INPUT_VALIDATION] = mode
        }
    }

    // Appearance preferences
    val theme: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME] ?: DEFAULT_THEME
    }

    suspend fun setTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_THEME] = theme
        }
    }

    val dynamicColors: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_DYNAMIC_COLORS] ?: true
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DYNAMIC_COLORS] = enabled
        }
    }

    val fontSize: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_FONT_SIZE] ?: DEFAULT_FONT_SIZE
    }

    suspend fun setFontSize(size: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_FONT_SIZE] = size
        }
    }

    // Learning preferences
    val autoAdvanceLessons: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTO_ADVANCE_LESSONS] ?: false
    }

    suspend fun setAutoAdvanceLessons(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTO_ADVANCE_LESSONS] = enabled
        }
    }

    val dailyReminders: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_DAILY_REMINDERS] ?: false
    }

    suspend fun setDailyReminders(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_DAILY_REMINDERS] = enabled
        }
    }

    // Reset all preferences
    suspend fun resetAllPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
