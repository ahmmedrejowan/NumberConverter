package com.rejowan.numberconverter.domain.model

data class PracticeSession(
    val id: String,
    val type: PracticeType,
    val problems: List<PracticeProblem>,
    val currentProblemIndex: Int,
    val score: Int,
    val streak: Int,
    val startedAt: Long,
    val completedAt: Long?
)

enum class PracticeType(val displayName: String, val description: String) {
    RANDOM_CHALLENGE("Random Challenge", "Random conversions across all bases"),
    BY_LESSON("By Lesson", "Practice problems related to specific lessons"),
    TIMED_QUIZ("Timed Quiz", "Complete as many as you can in the time limit"),
    DAILY_CHALLENGE("Daily Challenge", "A new challenge every day")
}

data class PracticeProblem(
    val id: String,
    val number: String,
    val fromBase: NumberBase,
    val toBase: NumberBase,
    val correctAnswer: String,
    val userAnswer: String?,
    val isCorrect: Boolean?,
    val timeSpentMs: Long?
)
