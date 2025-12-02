package com.rejowan.numberconverter.domain.usecase.learn

import com.rejowan.numberconverter.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow

data class ProgressSummary(
    val completedLessons: Int,
    val totalLessons: Int,
    val overallProgress: Float
)

class GetProgressSummaryUseCase(
    private val progressRepository: ProgressRepository
) {
    operator fun invoke(): Flow<ProgressSummary> {
        return kotlinx.coroutines.flow.combine(
            progressRepository.getCompletedCount(),
            progressRepository.getTotalProgressPercentage()
        ) { completed: Int, progress: Float ->
            ProgressSummary(
                completedLessons = completed,
                totalLessons = 18,
                overallProgress = progress
            )
        }
    }
}
