package com.rejowan.numberconverter.data.local.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lesson_progress")
data class ProgressEntity(
    @PrimaryKey
    val lessonId: String,
    val status: String, // NOT_STARTED, IN_PROGRESS, COMPLETED
    val completedSections: String, // JSON array of completed section indices
    val quizScore: Int = 0,
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
