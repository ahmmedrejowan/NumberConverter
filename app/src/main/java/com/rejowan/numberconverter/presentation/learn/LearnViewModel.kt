package com.rejowan.numberconverter.presentation.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.usecase.learn.GetLessonsUseCase
import com.rejowan.numberconverter.domain.usecase.learn.GetProgressSummaryUseCase
import com.rejowan.numberconverter.presentation.learn.state.LearnUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LearnViewModel(
    private val getLessonsUseCase: GetLessonsUseCase,
    private val getProgressSummaryUseCase: GetProgressSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LearnUiState>(LearnUiState.Loading)
    val uiState: StateFlow<LearnUiState> = _uiState.asStateFlow()

    init {
        loadLessons()
    }

    private fun loadLessons() {
        viewModelScope.launch {
            try {
                // Load lessons from JSON

                // Combine lessons and progress
                combine(
                    getLessonsUseCase(),
                    getProgressSummaryUseCase()
                ) { lessonsByCategory, progressSummary ->
                    LearnUiState.Success(
                        lessonsByCategory = lessonsByCategory,
                        progressSummary = progressSummary
                    )
                }.catch { error ->
                    _uiState.value = LearnUiState.Error(
                        error.message ?: "Failed to load lessons"
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = LearnUiState.Error(
                    e.message ?: "Failed to load lessons"
                )
            }
        }
    }

    fun retry() {
        _uiState.value = LearnUiState.Loading
        loadLessons()
    }
}
