package com.rejowan.numberconverter.domain.model

data class Exercise(
    val id: String,
    val problem: String,
    val correctAnswer: String,
    val difficulty: Difficulty,
    val fromBase: NumberBase? = null,
    val toBase: NumberBase? = null,
    val explanation: String? = null,
    val hints: List<String> = emptyList()
)

enum class Difficulty(val displayName: String, val multiplier: Float) {
    EASY("Easy", 1.0f),
    MEDIUM("Medium", 1.5f),
    HARD("Hard", 2.0f)
}
