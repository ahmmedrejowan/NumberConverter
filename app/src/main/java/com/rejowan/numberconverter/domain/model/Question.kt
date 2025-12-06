package com.rejowan.numberconverter.domain.model

sealed class Question {
    abstract val id: String
    abstract val questionText: String
    abstract val explanation: String
    abstract val hints: List<String>

    data class MultipleChoice(
        override val id: String,
        override val questionText: String,
        override val explanation: String,
        val options: List<String>,
        val correctAnswerIndex: Int,
        override val hints: List<String> = emptyList()
    ) : Question()

    data class FillBlank(
        override val id: String,
        override val questionText: String,
        override val explanation: String,
        val correctAnswer: String,
        val acceptableAnswers: List<String> = listOf(correctAnswer),
        override val hints: List<String> = emptyList()
    ) : Question()

    data class TrueFalse(
        override val id: String,
        override val questionText: String,
        override val explanation: String,
        val correctAnswer: Boolean,
        override val hints: List<String> = emptyList()
    ) : Question()
}
