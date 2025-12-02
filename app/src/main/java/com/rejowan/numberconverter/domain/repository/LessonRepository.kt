package com.rejowan.numberconverter.domain.repository

import com.rejowan.numberconverter.domain.model.Lesson
import com.rejowan.numberconverter.domain.model.LessonCategory

interface LessonRepository {
    suspend fun getAllLessons(): List<Lesson>
    suspend fun getLessonById(id: String): Lesson?
    suspend fun getLessonsByCategory(category: LessonCategory): List<Lesson>
    suspend fun searchLessons(query: String): List<Lesson>
}
