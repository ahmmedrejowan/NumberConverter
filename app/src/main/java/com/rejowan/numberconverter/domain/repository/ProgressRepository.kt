package com.rejowan.numberconverter.domain.repository

import com.rejowan.numberconverter.domain.model.LessonProgress
import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    fun getAllProgress(): Flow<List<LessonProgress>>
    fun getProgressByLessonId(lessonId: String): Flow<LessonProgress?>
    fun getCompletedCount(): Flow<Int>
    fun getTotalProgressPercentage(): Flow<Float>
    suspend fun updateProgress(progress: LessonProgress)
    suspend fun markSectionComplete(lessonId: String, sectionId: String)
    suspend fun saveQuizScore(lessonId: String, score: Int)
    suspend fun markLessonComplete(lessonId: String)
    suspend fun resetAllProgress()
}
