package com.rejowan.numberconverter.domain.model

data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val category: LessonCategory,
    val estimatedTimeMinutes: Int,
    val prerequisites: List<String>,
    val sections: List<LessonSection>,
    val order: Int
)

enum class LessonCategory(val displayName: String, val order: Int) {
    BEGINNER("Beginner", 0),
    INTERMEDIATE("Intermediate", 1),
    ADVANCED("Advanced", 2)
}

sealed class LessonSection {
    abstract val id: String
    abstract val title: String

    data class Theory(
        override val id: String,
        override val title: String,
        val content: String,
        val imageResId: Int? = null
    ) : LessonSection()

    data class Interactive(
        override val id: String,
        override val title: String,
        val description: String,
        val exercise: Exercise,
        val hints: List<String>
    ) : LessonSection()

    data class Practice(
        override val id: String,
        override val title: String,
        val exercises: List<Exercise>
    ) : LessonSection()

    data class Quiz(
        override val id: String,
        override val title: String,
        val questions: List<Question>
    ) : LessonSection()
}
