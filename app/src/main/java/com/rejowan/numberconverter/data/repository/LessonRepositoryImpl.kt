package com.rejowan.numberconverter.data.repository

import com.rejowan.numberconverter.data.local.LessonJsonParser
import com.rejowan.numberconverter.domain.model.Lesson
import com.rejowan.numberconverter.domain.model.LessonCategory
import com.rejowan.numberconverter.domain.repository.LessonRepository

class LessonRepositoryImpl(
    private val jsonParser: LessonJsonParser
) : LessonRepository {

    private var lessonsCache: List<Lesson>? = null

    override suspend fun getAllLessons(): List<Lesson> {
        if (lessonsCache == null) {
            lessonsCache = jsonParser.parseLessons()
        }
        return lessonsCache ?: emptyList()
    }

    override suspend fun getLessonById(id: String): Lesson? {
        return getAllLessons().find { it.id == id }
    }

    override suspend fun getLessonsByCategory(category: LessonCategory): List<Lesson> {
        return getAllLessons().filter { it.category == category }
    }

    override suspend fun searchLessons(query: String): List<Lesson> {
        return getAllLessons().filter { lesson ->
            lesson.title.contains(query, ignoreCase = true) ||
            lesson.description.contains(query, ignoreCase = true)
        }
    }
}
