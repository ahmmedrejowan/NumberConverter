package com.rejowan.numberconverter.presentation.practice

import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.usecase.practice.AnswerResult

sealed class PracticeUiState {
    object Initial : PracticeUiState()
    object Loading : PracticeUiState()

    data class PracticeSession(
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
        val fromBase: NumberBase? = null,
        val toBase: NumberBase? = null,
        val answerResult: AnswerResult? = null,
        val showExplanation: Boolean = false
    ) : PracticeUiState()

    data class SessionComplete(
        val correctAnswers: Int,
        val totalQuestions: Int,
        val percentage: Float,
        val longestStreak: Int,
        val points: Int,
        val difficulty: Difficulty
    ) : PracticeUiState()
}
