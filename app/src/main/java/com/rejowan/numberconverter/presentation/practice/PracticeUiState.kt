package com.rejowan.numberconverter.presentation.practice

import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.usecase.practice.AnswerResult

enum class PracticeType(val displayName: String) {
    CONVERSION("Conversion"),
    CALCULATION("Calculation")
}

sealed class PracticeUiState {
    data object SelectType : PracticeUiState()

    data class Setup(
        val practiceType: PracticeType,
        val selectedDifficulty: Difficulty = Difficulty.MEDIUM,
        val selectedQuestionCount: Int = 10
    ) : PracticeUiState()

    data object Loading : PracticeUiState()

    data class Quiz(
        val practiceType: PracticeType,
        val currentExercise: Exercise,
        val currentQuestionIndex: Int,
        val totalQuestions: Int,
        val userAnswer: String = "",
        val showHints: Boolean = false,
        val usedHints: Int = 0,
        val correctAnswers: Int = 0,
        val currentStreak: Int = 0,
        val longestStreak: Int = 0,
        val difficulty: Difficulty,
        val answerResult: AnswerResult? = null
    ) : PracticeUiState()

    data class Complete(
        val practiceType: PracticeType,
        val correctAnswers: Int,
        val totalQuestions: Int,
        val percentage: Float,
        val longestStreak: Int,
        val points: Int,
        val difficulty: Difficulty
    ) : PracticeUiState()
}
