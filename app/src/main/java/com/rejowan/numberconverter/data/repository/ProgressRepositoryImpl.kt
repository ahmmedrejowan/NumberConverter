package com.rejowan.numberconverter.data.repository

import com.rejowan.numberconverter.data.local.database.dao.ProgressDao
import com.rejowan.numberconverter.data.local.database.entity.ProgressEntity
import com.rejowan.numberconverter.domain.model.LessonProgress
import com.rejowan.numberconverter.domain.model.ProgressStatus
import com.rejowan.numberconverter.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

class ProgressRepositoryImpl(
    private val progressDao: ProgressDao
) : ProgressRepository {

    override fun getAllProgress(): Flow<List<LessonProgress>> {
        return progressDao.getAllProgress().map { entities ->
            entities.map { it.toProgress() }
        }
    }

    override fun getProgressByLessonId(lessonId: String): Flow<LessonProgress?> {
        return progressDao.getProgressByLessonId(lessonId).map { entity ->
            entity?.toProgress()
        }
    }

    override fun getCompletedCount(): Flow<Int> {
        return progressDao.getCompletedCount()
    }

    override fun getTotalProgressPercentage(): Flow<Float> {
        return progressDao.getCompletedCount().map { count ->
            count.toFloat() / 18f * 100f
        }
    }

    override suspend fun updateProgress(progress: LessonProgress) {
        progressDao.insert(progress.toEntity())
    }

    override suspend fun markSectionComplete(lessonId: String, sectionId: String) {
        progressDao.insert(
            ProgressEntity(
                lessonId = lessonId,
                status = "IN_PROGRESS",
                completedSections = JSONArray(listOf(sectionId)).toString(),
                quizScore = 0,
                lastAccessedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun saveQuizScore(lessonId: String, score: Int) {
        progressDao.insert(
            ProgressEntity(
                lessonId = lessonId,
                status = "IN_PROGRESS",
                completedSections = "[]",
                quizScore = score,
                lastAccessedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun markLessonComplete(lessonId: String) {
        progressDao.insert(
            ProgressEntity(
                lessonId = lessonId,
                status = "COMPLETED",
                completedSections = "[]",
                quizScore = 100,
                lastAccessedAt = System.currentTimeMillis(),
                completedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun resetAllProgress() {
        progressDao.deleteAll()
    }

    private fun ProgressEntity.toProgress(): LessonProgress {
        val sections = try {
            val jsonArray = JSONArray(completedSections)
            (0 until jsonArray.length()).map { jsonArray.getString(it) }
        } catch (e: Exception) {
            emptyList()
        }

        return LessonProgress(
            lessonId = lessonId,
            status = ProgressStatus.valueOf(status),
            completedSections = sections,
            quizScore = if (quizScore > 0) quizScore else null,
            lastAccessedAt = lastAccessedAt,
            completedAt = completedAt
        )
    }

    private fun LessonProgress.toEntity(): ProgressEntity {
        val sectionsJson = JSONArray(completedSections).toString()
        return ProgressEntity(
            lessonId = lessonId,
            status = status.name,
            completedSections = sectionsJson,
            quizScore = quizScore ?: 0,
            lastAccessedAt = lastAccessedAt,
            completedAt = completedAt
        )
    }
}
