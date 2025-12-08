package com.rejowan.numberconverter.data.local

import android.content.Context
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.Lesson
import com.rejowan.numberconverter.domain.model.LessonCategory
import com.rejowan.numberconverter.domain.model.LessonSection
import com.rejowan.numberconverter.domain.model.Question
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LessonJsonParser(private val context: Context) {

    suspend fun parseLessons(): List<Lesson> = withContext(Dispatchers.IO) {
        try {
            // List all lesson files in the lessons directory
            val lessonFiles = context.assets.list("lessons")
                ?.filter { it.startsWith("lesson_") && it.endsWith(".json") }
                ?.sortedBy { fileName ->
                    // Extract the number from "lesson_X.json" and sort numerically
                    fileName.removePrefix("lesson_").removeSuffix(".json").toIntOrNull() ?: 0
                } ?: emptyList()

            // Parse each lesson file
            lessonFiles.mapNotNull { fileName ->
                try {
                    val jsonString = context.assets.open("lessons/$fileName")
                        .bufferedReader()
                        .use { it.readText() }

                    val jsonObject = JSONObject(jsonString)
                    parseLesson(jsonObject)
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun parseLesson(json: JSONObject): Lesson {
        val sectionsArray = json.getJSONArray("sections")
        val sections = (0 until sectionsArray.length()).mapIndexed { index, _ ->
            parseSection(sectionsArray.getJSONObject(index), index)
        }

        val prerequisitesArray = json.optJSONArray("prerequisites") ?: JSONArray()
        val prerequisites = (0 until prerequisitesArray.length()).map { index ->
            prerequisitesArray.getString(index)
        }

        return Lesson(
            id = json.getString("id"),
            title = json.getString("title"),
            description = json.getString("description"),
            category = LessonCategory.valueOf(json.getString("category")),
            estimatedTimeMinutes = json.getInt("estimatedTimeMinutes"),
            prerequisites = prerequisites,
            sections = sections,
            order = json.optInt("order", 0)
        )
    }

    private fun parseSection(json: JSONObject, index: Int): LessonSection {
        val sectionId = "section_$index"
        val title = json.optString("title", "Section $index")

        return when (json.getString("type")) {
            "theory" -> LessonSection.Theory(
                id = sectionId,
                title = title,
                content = json.getString("content"),
                imageResId = null
            )
            "interactive" -> {
                val exerciseJson = json.getJSONObject("exercise")
                val exercise = parseExercise(exerciseJson)

                LessonSection.Interactive(
                    id = sectionId,
                    title = title,
                    description = json.optString("description", ""),
                    exercise = exercise,
                    hints = parseStringArray(json.optJSONArray("hints"))
                )
            }
            "practice" -> {
                val exercisesArray = json.getJSONArray("exercises")
                val exercises = (0 until exercisesArray.length()).map { i ->
                    parseExercise(exercisesArray.getJSONObject(i))
                }
                LessonSection.Practice(
                    id = sectionId,
                    title = title,
                    exercises = exercises
                )
            }
            "quiz" -> {
                val questionsArray = json.getJSONArray("questions")
                val questions = (0 until questionsArray.length()).map { i ->
                    parseQuestion(questionsArray.getJSONObject(i))
                }
                LessonSection.Quiz(
                    id = sectionId,
                    title = title,
                    questions = questions
                )
            }
            else -> LessonSection.Theory(
                id = sectionId,
                title = "Unknown",
                content = "Unknown section type",
                imageResId = null
            )
        }
    }

    private fun parseExercise(json: JSONObject): Exercise {
        val difficultyString = json.optString("difficulty", "MEDIUM")
        val difficulty = try {
            Difficulty.valueOf(difficultyString)
        } catch (e: Exception) {
            Difficulty.MEDIUM
        }

        return Exercise(
            id = json.optString("id", "exercise_${System.currentTimeMillis()}"),
            problem = json.getString("problem"),
            correctAnswer = json.getString("correctAnswer"),
            difficulty = difficulty,
            fromBase = null,
            toBase = null,
            explanation = json.optString("explanation", null),
            hints = parseStringArray(json.optJSONArray("hints"))
        )
    }

    private fun parseQuestion(json: JSONObject): Question {
        val questionId = json.optString("id", "question_${System.currentTimeMillis()}")
        val hints = parseStringArray(json.optJSONArray("hints"))

        return when (json.getString("type")) {
            "MultipleChoice" -> {
                val options = parseStringArray(json.getJSONArray("options"))
                val correctIndex = json.getInt("correctAnswerIndex")
                Question.MultipleChoice(
                    id = questionId,
                    questionText = json.getString("questionText"),
                    options = options,
                    correctAnswerIndex = correctIndex,
                    explanation = json.getString("explanation"),
                    hints = hints
                )
            }
            "FillBlank" -> Question.FillBlank(
                id = questionId,
                questionText = json.getString("questionText"),
                correctAnswer = json.getString("correctAnswer"),
                acceptableAnswers = parseStringArray(json.optJSONArray("acceptableAnswers")),
                explanation = json.getString("explanation"),
                hints = hints
            )
            "TrueFalse" -> Question.TrueFalse(
                id = questionId,
                questionText = json.getString("questionText"),
                correctAnswer = json.getBoolean("correctAnswer"),
                explanation = json.getString("explanation"),
                hints = hints
            )
            else -> Question.TrueFalse(
                id = questionId,
                questionText = "Unknown question type",
                correctAnswer = false,
                explanation = "",
                hints = emptyList()
            )
        }
    }

    private fun parseStringArray(jsonArray: JSONArray?): List<String> {
        if (jsonArray == null) return emptyList()
        return (0 until jsonArray.length()).map { index ->
            jsonArray.getString(index)
        }
    }
}
