package com.rejowan.numberconverter.domain.model

data class LessonProgress(
    val lessonId: String,
    val status: ProgressStatus,
    val completedSections: List<String>,
    val quizScore: Int?,
    val lastAccessedAt: Long,
    val completedAt: Long?
)

enum class ProgressStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED
}
