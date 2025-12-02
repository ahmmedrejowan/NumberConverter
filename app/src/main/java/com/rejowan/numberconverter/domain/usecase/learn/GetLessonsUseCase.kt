package com.rejowan.numberconverter.domain.usecase.learn

import com.rejowan.numberconverter.domain.model.Lesson
import com.rejowan.numberconverter.domain.model.LessonCategory
import com.rejowan.numberconverter.domain.model.ProgressStatus
import com.rejowan.numberconverter.domain.repository.LessonRepository
import com.rejowan.numberconverter.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class LessonWithProgress(
    val lesson: Lesson,
    val isLocked: Boolean,
    val progressPercentage: Float
)

class GetLessonsUseCase(
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository
) {
    operator fun invoke(): Flow<Map<LessonCategory, List<LessonWithProgress>>> {
        return progressRepository.getAllProgress().map { progressList ->
            val lessons = lessonRepository.getAllLessons()
            val progressMap = progressList.associateBy { it.lessonId }
            val completedLessonIds = progressList
                .filter { it.status == ProgressStatus.COMPLETED }
                .map { it.lessonId }
                .toSet()

            lessons.groupBy { it.category }.mapValues { (_, categoryLessons) ->
                categoryLessons.map { lesson ->
                    val isLocked = lesson.prerequisites.isNotEmpty() &&
                            !lesson.prerequisites.all { prereqId -> completedLessonIds.contains(prereqId) }

                    val progress = progressMap[lesson.id]
                    val progressPercentage = when {
                        progress == null -> 0f
                        progress.status == ProgressStatus.COMPLETED -> 100f
                        progress.completedSections.isNotEmpty() ->
                            (progress.completedSections.size.toFloat() / lesson.sections.size) * 100f
                        else -> 0f
                    }

                    LessonWithProgress(
                        lesson = lesson,
                        isLocked = isLocked,
                        progressPercentage = progressPercentage
                    )
                }
            }
        }
    }
}
