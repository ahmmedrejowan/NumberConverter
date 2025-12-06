package com.rejowan.numberconverter.presentation.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.model.Lesson
import com.rejowan.numberconverter.domain.model.LessonProgress
import com.rejowan.numberconverter.domain.model.LessonSection
import com.rejowan.numberconverter.domain.model.ProgressStatus
import com.rejowan.numberconverter.domain.repository.LessonRepository
import com.rejowan.numberconverter.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class LessonDetailUiState {
    data object Loading : LessonDetailUiState()
    data class Error(val message: String) : LessonDetailUiState()
    data class Success(
        val lesson: Lesson,
        val currentSectionIndex: Int = 0,
        val completedSections: Set<String> = emptySet(),
        val quizAnswers: Map<String, Any> = emptyMap(),
        val quizScore: Int? = null,
        val showQuizResults: Boolean = false,
        val lessonProgress: LessonProgress? = null,
        val shouldNavigateBack: Boolean = false
    ) : LessonDetailUiState() {
        val currentSection: LessonSection
            get() = lesson.sections[currentSectionIndex]

        val isFirstSection: Boolean
            get() = currentSectionIndex == 0

        val isLastSection: Boolean
            get() = currentSectionIndex == lesson.sections.size - 1

        val canProceedToNext: Boolean
            get() = when (currentSection) {
                is LessonSection.Quiz -> quizScore != null && quizScore >= 70
                is LessonSection.Theory -> true // Theory sections can always proceed
                else -> currentSection.id in completedSections
            }

        val overallProgress: Float
            get() = completedSections.size.toFloat() / lesson.sections.size

        val allSectionsCompleted: Boolean
            get() = completedSections.size == lesson.sections.size
    }
}

class LessonDetailViewModel(
    private val lessonId: String,
    private val lessonRepository: LessonRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LessonDetailUiState>(LessonDetailUiState.Loading)
    val uiState: StateFlow<LessonDetailUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        viewModelScope.launch {
            try {
                val lesson = lessonRepository.getLessonById(lessonId)
                if (lesson == null) {
                    _uiState.value = LessonDetailUiState.Error("Lesson not found")
                    return@launch
                }

                progressRepository.getProgressByLessonId(lessonId).collect { progress ->
                    val completedSections = progress?.completedSections?.toSet() ?: emptySet()

                    // Find the first incomplete section to resume from
                    val resumeSectionIndex = lesson.sections.indexOfFirst { section ->
                        section.id !in completedSections
                    }.takeIf { it >= 0 } ?: 0 // Default to first section if all completed

                    _uiState.value = LessonDetailUiState.Success(
                        lesson = lesson,
                        currentSectionIndex = resumeSectionIndex,
                        completedSections = completedSections,
                        quizScore = progress?.quizScore,
                        lessonProgress = progress
                    )
                }
            } catch (e: Exception) {
                _uiState.value = LessonDetailUiState.Error(
                    e.message ?: "Failed to load lesson"
                )
            }
        }
    }

    fun nextSection() {
        val currentState = _uiState.value as? LessonDetailUiState.Success ?: return

        // Auto-complete Theory sections when Next is pressed
        if (currentState.currentSection is LessonSection.Theory &&
            currentState.currentSection.id !in currentState.completedSections) {
            markSectionComplete(currentState.currentSection.id)
        }

        if (!currentState.isLastSection) {
            _uiState.update {
                currentState.copy(
                    currentSectionIndex = currentState.currentSectionIndex + 1,
                    showQuizResults = false
                )
            }
        }
    }

    fun previousSection() {
        val currentState = _uiState.value as? LessonDetailUiState.Success ?: return
        if (!currentState.isFirstSection) {
            _uiState.update {
                currentState.copy(
                    currentSectionIndex = currentState.currentSectionIndex - 1,
                    showQuizResults = false
                )
            }
        }
    }

    fun markSectionComplete(sectionId: String, autoNavigate: Boolean = false) {
        viewModelScope.launch {
            val currentState = _uiState.value as? LessonDetailUiState.Success ?: return@launch

            progressRepository.markSectionComplete(lessonId, sectionId)

            val newCompletedSections = currentState.completedSections + sectionId

            // Check if all sections are completed
            if (newCompletedSections.size == currentState.lesson.sections.size) {
                progressRepository.markLessonComplete(lessonId)
                // Don't auto-navigate yet, let user click Finish button
            } else {
                // Update progress status to IN_PROGRESS
                val updatedProgress = LessonProgress(
                    lessonId = lessonId,
                    status = ProgressStatus.IN_PROGRESS,
                    completedSections = newCompletedSections.toList(),
                    quizScore = currentState.quizScore,
                    lastAccessedAt = System.currentTimeMillis(),
                    completedAt = null
                )
                progressRepository.updateProgress(updatedProgress)

                // Auto-navigate to next section if requested
                if (autoNavigate && !currentState.isLastSection) {
                    _uiState.update {
                        currentState.copy(
                            currentSectionIndex = currentState.currentSectionIndex + 1,
                            showQuizResults = false
                        )
                    }
                }
            }
        }
    }

    fun finishLesson() {
        val currentState = _uiState.value as? LessonDetailUiState.Success ?: return
        _uiState.update {
            currentState.copy(shouldNavigateBack = true)
        }
    }

    fun onNavigatedBack() {
        val currentState = _uiState.value as? LessonDetailUiState.Success ?: return
        _uiState.update {
            currentState.copy(shouldNavigateBack = false)
        }
    }

    fun submitQuizAnswers(answers: Map<String, Any>) {
        val currentState = _uiState.value as? LessonDetailUiState.Success ?: return
        val quizSection = currentState.currentSection as? LessonSection.Quiz ?: return

        // Calculate score
        var correctCount = 0
        quizSection.questions.forEach { question ->
            val userAnswer = answers[question.id]
            val isCorrect = when (question) {
                is com.rejowan.numberconverter.domain.model.Question.MultipleChoice -> {
                    userAnswer == question.correctAnswerIndex
                }
                is com.rejowan.numberconverter.domain.model.Question.FillBlank -> {
                    userAnswer?.toString()?.trim()?.lowercase() in question.acceptableAnswers.map { it.lowercase() }
                }
                is com.rejowan.numberconverter.domain.model.Question.TrueFalse -> {
                    userAnswer == question.correctAnswer
                }
            }
            if (isCorrect) correctCount++
        }

        val score = (correctCount * 100) / quizSection.questions.size

        viewModelScope.launch {
            progressRepository.saveQuizScore(lessonId, score)

            _uiState.update {
                currentState.copy(
                    quizAnswers = answers,
                    quizScore = score,
                    showQuizResults = true
                )
            }

            // If score is passing (>=70), mark section as complete and auto-navigate
            if (score >= 70) {
                markSectionComplete(quizSection.id, autoNavigate = true)
            }
        }
    }

    fun retryQuiz() {
        val currentState = _uiState.value as? LessonDetailUiState.Success ?: return
        _uiState.update {
            currentState.copy(
                quizAnswers = emptyMap(),
                quizScore = null,
                showQuizResults = false
            )
        }
    }

    fun retry() {
        _uiState.value = LessonDetailUiState.Loading
        loadLesson()
    }
}
