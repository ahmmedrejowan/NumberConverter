package com.rejowan.numberconverter.presentation.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.generator.ProblemGenerator
import com.rejowan.numberconverter.domain.usecase.practice.CalculateScoreUseCase
import com.rejowan.numberconverter.domain.usecase.practice.CheckAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PracticeViewModel(
    private val problemGenerator: ProblemGenerator,
    private val checkAnswerUseCase: CheckAnswerUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeUiState>(PracticeUiState.SelectType)
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var problems: List<Exercise> = emptyList()
    private var currentPracticeType: PracticeType = PracticeType.CONVERSION

    fun selectPracticeType(type: PracticeType) {
        currentPracticeType = type
        _uiState.value = PracticeUiState.Setup(practiceType = type)
    }

    fun updateDifficulty(difficulty: Difficulty) {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Setup) {
            _uiState.value = currentState.copy(selectedDifficulty = difficulty)
        }
    }

    fun updateQuestionCount(count: Int) {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Setup) {
            _uiState.value = currentState.copy(selectedQuestionCount = count)
        }
    }

    fun startPractice() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Setup) {
            viewModelScope.launch {
                _uiState.value = PracticeUiState.Loading

                problems = when (currentState.practiceType) {
                    PracticeType.CONVERSION -> problemGenerator.generateConversionBatch(
                        count = currentState.selectedQuestionCount,
                        difficulty = currentState.selectedDifficulty
                    )
                    PracticeType.CALCULATION -> problemGenerator.generateCalculationBatch(
                        count = currentState.selectedQuestionCount,
                        difficulty = currentState.selectedDifficulty
                    )
                }

                if (problems.isNotEmpty()) {
                    _uiState.value = PracticeUiState.Quiz(
                        practiceType = currentState.practiceType,
                        currentExercise = problems[0],
                        currentQuestionIndex = 0,
                        totalQuestions = problems.size,
                        difficulty = currentState.selectedDifficulty
                    )
                }
            }
        }
    }

    fun onAnswerChanged(answer: String) {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Quiz) {
            _uiState.value = currentState.copy(userAnswer = answer)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Quiz && currentState.answerResult == null) {
            val result = checkAnswerUseCase(currentState.currentExercise, currentState.userAnswer)

            val newCorrectAnswers = if (result.isCorrect) {
                currentState.correctAnswers + 1
            } else {
                currentState.correctAnswers
            }

            val newStreak = if (result.isCorrect) {
                currentState.currentStreak + 1
            } else {
                0
            }

            val newLongestStreak = maxOf(currentState.longestStreak, newStreak)

            _uiState.value = currentState.copy(
                answerResult = result,
                correctAnswers = newCorrectAnswers,
                currentStreak = newStreak,
                longestStreak = newLongestStreak
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Quiz) {
            val nextIndex = currentState.currentQuestionIndex + 1

            if (nextIndex < problems.size) {
                _uiState.value = PracticeUiState.Quiz(
                    practiceType = currentState.practiceType,
                    currentExercise = problems[nextIndex],
                    currentQuestionIndex = nextIndex,
                    totalQuestions = problems.size,
                    correctAnswers = currentState.correctAnswers,
                    currentStreak = currentState.currentStreak,
                    longestStreak = currentState.longestStreak,
                    difficulty = currentState.difficulty
                )
            } else {
                val score = calculateScoreUseCase(
                    correctAnswers = currentState.correctAnswers,
                    totalQuestions = currentState.totalQuestions,
                    currentStreak = currentState.currentStreak,
                    longestStreak = currentState.longestStreak
                )

                _uiState.value = PracticeUiState.Complete(
                    practiceType = currentState.practiceType,
                    correctAnswers = score.correctAnswers,
                    totalQuestions = score.totalQuestions,
                    percentage = score.percentage,
                    longestStreak = score.streak,
                    points = score.points,
                    difficulty = currentState.difficulty
                )
            }
        }
    }

    fun toggleHints() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Quiz) {
            _uiState.value = currentState.copy(
                showHints = !currentState.showHints,
                usedHints = if (!currentState.showHints) currentState.usedHints + 1 else currentState.usedHints
            )
        }
    }

    fun goBackToSetup() {
        _uiState.value = PracticeUiState.Setup(practiceType = currentPracticeType)
    }

    fun goBackToTypeSelection() {
        _uiState.value = PracticeUiState.SelectType
        problems = emptyList()
    }

    fun restartPractice() {
        val currentState = _uiState.value
        val practiceType = when (currentState) {
            is PracticeUiState.Complete -> currentState.practiceType
            is PracticeUiState.Quiz -> currentState.practiceType
            else -> currentPracticeType
        }
        val difficulty = when (currentState) {
            is PracticeUiState.Complete -> currentState.difficulty
            is PracticeUiState.Quiz -> currentState.difficulty
            else -> Difficulty.MEDIUM
        }

        _uiState.value = PracticeUiState.Setup(
            practiceType = practiceType,
            selectedDifficulty = difficulty
        )
    }
}
