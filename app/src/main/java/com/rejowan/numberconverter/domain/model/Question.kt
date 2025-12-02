package com.rejowan.numberconverter.domain.model

sealed class Question {
    abstract val id: String
    abstract val questionText: String
    abstract val explanation: String

    data class MultipleChoice(
        override val id: String,
        override val questionText: String,
        override val explanation: String,
        val options: List<String>,
        val correctAnswerIndex: Int
    ) : Question()

    data class FillBlank(
        override val id: String,
        override val questionText: String,
        override val explanation: String,
        val correctAnswer: String,
        val acceptableAnswers: List<String> = listOf(correctAnswer)
    ) : Question()

    data class TrueFalse(
        override val id: String,
        override val questionText: String,
        override val explanation: String,
        val correctAnswer: Boolean
    ) : Question()
}
