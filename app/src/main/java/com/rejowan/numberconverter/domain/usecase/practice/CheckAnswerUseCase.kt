package com.rejowan.numberconverter.domain.usecase.practice

import com.rejowan.numberconverter.domain.model.Exercise

data class AnswerResult(
    val isCorrect: Boolean,
    val userAnswer: String,
    val correctAnswer: String,
    val explanation: String
)

class CheckAnswerUseCase {
    operator fun invoke(exercise: Exercise, userAnswer: String): AnswerResult {
        val normalizedUserAnswer = userAnswer.trim().uppercase()
        val normalizedCorrectAnswer = exercise.correctAnswer.trim().uppercase()

        val isCorrect = normalizedUserAnswer == normalizedCorrectAnswer

        return AnswerResult(
            isCorrect = isCorrect,
            userAnswer = normalizedUserAnswer,
            correctAnswer = normalizedCorrectAnswer,
            explanation = exercise.explanation ?: "No explanation available"
        )
    }
}
