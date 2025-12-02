package com.rejowan.numberconverter.presentation.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.usecase.practice.CalculateScoreUseCase
import com.rejowan.numberconverter.domain.usecase.practice.CheckAnswerUseCase
import com.rejowan.numberconverter.domain.usecase.practice.GeneratePracticeProblemsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PracticeViewModel(
    private val generateProblemsUseCase: GeneratePracticeProblemsUseCase,
    private val checkAnswerUseCase: CheckAnswerUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PracticeUiState>(PracticeUiState.Initial)
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var problems: List<Exercise> = emptyList()

    fun startPracticeSession(
        difficulty: Difficulty,
        questionCount: Int = 10,
        fromBase: NumberBase? = null,
        toBase: NumberBase? = null
    ) {
        viewModelScope.launch {
            _uiState.value = PracticeUiState.Loading

            problems = generateProblemsUseCase(
                count = questionCount,
                difficulty = difficulty,
                fromBase = fromBase,
                toBase = toBase
            )

            if (problems.isNotEmpty()) {
                _uiState.value = PracticeUiState.PracticeSession(
                    currentExercise = problems[0],
                    currentQuestionIndex = 0,
                    totalQuestions = problems.size,
                    difficulty = difficulty,
                    fromBase = fromBase,
                    toBase = toBase
                )
            }
        }
    }

    fun onAnswerChanged(answer: String) {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.PracticeSession) {
            _uiState.value = currentState.copy(userAnswer = answer)
        }
    }

    fun submitAnswer() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.PracticeSession) {
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
                showExplanation = true,
                correctAnswers = newCorrectAnswers,
                currentStreak = newStreak,
                longestStreak = newLongestStreak
            )
        }
    }

    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.PracticeSession) {
            val nextIndex = currentState.currentQuestionIndex + 1

            if (nextIndex < problems.size) {
                _uiState.value = PracticeUiState.PracticeSession(
                    currentExercise = problems[nextIndex],
                    currentQuestionIndex = nextIndex,
                    totalQuestions = problems.size,
                    correctAnswers = currentState.correctAnswers,
                    currentStreak = currentState.currentStreak,
                    longestStreak = currentState.longestStreak,
                    difficulty = currentState.difficulty,
                    fromBase = currentState.fromBase,
                    toBase = currentState.toBase
                )
            } else {
                val score = calculateScoreUseCase(
                    correctAnswers = currentState.correctAnswers,
                    totalQuestions = currentState.totalQuestions,
                    currentStreak = currentState.currentStreak,
                    longestStreak = currentState.longestStreak
                )

                _uiState.value = PracticeUiState.SessionComplete(
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
        if (currentState is PracticeUiState.PracticeSession) {
            _uiState.value = currentState.copy(
                showHints = !currentState.showHints,
                usedHints = if (!currentState.showHints) currentState.usedHints + 1 else currentState.usedHints
            )
        }
    }

    fun resetSession() {
        _uiState.value = PracticeUiState.Initial
        problems = emptyList()
    }
}
