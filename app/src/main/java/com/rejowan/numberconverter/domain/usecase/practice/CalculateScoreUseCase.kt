package com.rejowan.numberconverter.domain.usecase.practice

data class PracticeScore(
    val correctAnswers: Int,
    val totalQuestions: Int,
    val percentage: Float,
    val streak: Int,
    val points: Int
)

class CalculateScoreUseCase {
    operator fun invoke(
        correctAnswers: Int,
        totalQuestions: Int,
        currentStreak: Int,
        longestStreak: Int
    ): PracticeScore {
        val percentage = if (totalQuestions > 0) {
            (correctAnswers.toFloat() / totalQuestions.toFloat()) * 100f
        } else {
            0f
        }

        // Points calculation: base points + streak bonus
        val basePoints = correctAnswers * 10
        val streakBonus = longestStreak * 5
        val totalPoints = basePoints + streakBonus

        return PracticeScore(
            correctAnswers = correctAnswers,
            totalQuestions = totalQuestions,
            percentage = percentage,
            streak = currentStreak,
            points = totalPoints
        )
    }
}
