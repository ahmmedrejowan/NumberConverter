package com.rejowan.numberconverter.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.usecase.history.DeleteHistoryUseCase
import com.rejowan.numberconverter.domain.usecase.settings.GetSettingsUseCase
import com.rejowan.numberconverter.domain.usecase.settings.UpdateSettingUseCase
import com.rejowan.numberconverter.presentation.settings.state.SettingsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateSettingUseCase: UpdateSettingUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            getSettingsUseCase().collect { settings ->
                _uiState.update {
                    it.copy(
                        theme = settings.theme,
                        dynamicColors = settings.dynamicColors,
                        fontSize = settings.fontSize,
                        decimalPlaces = settings.decimalPlaces,
                        autoSaveHistory = settings.autoSaveHistory,
                        showExplanations = settings.showExplanations,
                        inputValidation = settings.inputValidation,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch { updateSettingUseCase.updateTheme(theme) }
    }

    fun updateDynamicColors(enabled: Boolean) {
        viewModelScope.launch { updateSettingUseCase.updateDynamicColors(enabled) }
    }

    fun updateFontSize(size: String) {
        viewModelScope.launch { updateSettingUseCase.updateFontSize(size) }
    }

    fun updateDecimalPlaces(places: Int) {
        viewModelScope.launch { updateSettingUseCase.updateDecimalPlaces(places) }
    }

    fun updateAutoSaveHistory(enabled: Boolean) {
        viewModelScope.launch { updateSettingUseCase.updateAutoSaveHistory(enabled) }
    }

    fun updateShowExplanations(enabled: Boolean) {
        viewModelScope.launch { updateSettingUseCase.updateShowExplanations(enabled) }
    }

    fun clearHistory() {
        viewModelScope.launch { deleteHistoryUseCase.deleteAll() }
    }

    fun resetSettings() {
        viewModelScope.launch { updateSettingUseCase.resetAllSettings() }
    }
}
