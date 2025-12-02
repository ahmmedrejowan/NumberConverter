package com.rejowan.numberconverter.presentation.learn.state

import com.rejowan.numberconverter.domain.model.LessonCategory
import com.rejowan.numberconverter.domain.usecase.learn.LessonWithProgress
import com.rejowan.numberconverter.domain.usecase.learn.ProgressSummary

sealed class LearnUiState {
    object Loading : LearnUiState()
    data class Success(
        val lessonsByCategory: Map<LessonCategory, List<LessonWithProgress>>,
        val progressSummary: ProgressSummary
    ) : LearnUiState()
    data class Error(val message: String) : LearnUiState()
}
