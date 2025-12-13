package com.rejowan.numberconverter.presentation.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.generator.ProblemGenerator
import com.rejowan.numberconverter.domain.usecase.practice.CalculateScoreUseCase
import com.rejowan.numberconverter.domain.usecase.practice.CheckAnswerUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    private var timerJob: Job? = null
    private var examStartTimeMillis: Long = 0L

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

    fun updateMcqSubType(subType: McqSubType) {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Setup) {
            _uiState.value = currentState.copy(mcqSubType = subType)
        }
    }

    fun updateExamTime(minutes: Int) {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Setup) {
            _uiState.value = currentState.copy(examTimeMinutes = minutes)
        }
    }

    fun startPractice() {
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Setup) {
            viewModelScope.launch {
                _uiState.value = PracticeUiState.Loading

                val mcqType = when (currentState.mcqSubType) {
                    McqSubType.CONVERSION -> ProblemGenerator.McqType.CONVERSION
                    McqSubType.CALCULATION -> ProblemGenerator.McqType.CALCULATION
                    McqSubType.MIX -> ProblemGenerator.McqType.MIX
                }

                problems = when (currentState.practiceType) {
                    PracticeType.CONVERSION -> problemGenerator.generateConversionBatch(
                        count = currentState.selectedQuestionCount,
                        difficulty = currentState.selectedDifficulty
                    )
                    PracticeType.CALCULATION -> problemGenerator.generateCalculationBatch(
                        count = currentState.selectedQuestionCount,
                        difficulty = currentState.selectedDifficulty
                    )
                    PracticeType.MCQ -> problemGenerator.generateMcqBatch(
                        count = currentState.selectedQuestionCount,
                        difficulty = currentState.selectedDifficulty,
                        mcqType = mcqType
                    )
                    PracticeType.EXAM -> problemGenerator.generateMcqBatch(
                        count = currentState.selectedQuestionCount,
                        difficulty = currentState.selectedDifficulty,
                        mcqType = mcqType
                    )
                }

                if (problems.isNotEmpty()) {
                    val isExam = currentState.practiceType == PracticeType.EXAM
                    val examTimeMillis = if (isExam) currentState.examTimeMinutes * 60 * 1000L else 0L

                    if (isExam) {
                        examStartTimeMillis = System.currentTimeMillis()
                        startExamTimer(examTimeMillis)
                    }

                    _uiState.value = PracticeUiState.Quiz(
                        practiceType = currentState.practiceType,
                        currentExercise = problems[0],
                        currentQuestionIndex = 0,
                        totalQuestions = problems.size,
                        difficulty = currentState.selectedDifficulty,
                        mcqSubType = currentState.mcqSubType,
                        isExamMode = isExam,
                        examTimeMillis = examTimeMillis,
                        remainingTimeMillis = examTimeMillis
                    )
                }
            }
        }
    }

    private fun startExamTimer(totalTimeMillis: Long) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var remainingTime = totalTimeMillis
            while (remainingTime > 0) {
                delay(1000L)
                remainingTime -= 1000L

                val currentState = _uiState.value
                if (currentState is PracticeUiState.Quiz && currentState.isExamMode) {
                    _uiState.value = currentState.copy(remainingTimeMillis = remainingTime)

                    if (remainingTime <= 0) {
                        finishExam()
                    }
                } else {
                    break
                }
            }
        }
    }

    private fun finishExam() {
        timerJob?.cancel()
        val currentState = _uiState.value
        if (currentState is PracticeUiState.Quiz) {
            val timeTaken = ((System.currentTimeMillis() - examStartTimeMillis) / 1000).toInt()

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
                difficulty = currentState.difficulty,
                isExamMode = true,
                timeTakenSeconds = timeTaken
            )
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
                    difficulty = currentState.difficulty,
                    mcqSubType = currentState.mcqSubType,
                    isExamMode = currentState.isExamMode,
                    examTimeMillis = currentState.examTimeMillis,
                    remainingTimeMillis = currentState.remainingTimeMillis
                )
            } else {
                timerJob?.cancel()
                val timeTaken = if (currentState.isExamMode) {
                    ((System.currentTimeMillis() - examStartTimeMillis) / 1000).toInt()
                } else 0

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
                    difficulty = currentState.difficulty,
                    isExamMode = currentState.isExamMode,
                    timeTakenSeconds = timeTaken
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
        timerJob?.cancel()
        _uiState.value = PracticeUiState.Setup(practiceType = currentPracticeType)
    }

    fun goBackToTypeSelection() {
        timerJob?.cancel()
        _uiState.value = PracticeUiState.SelectType
        problems = emptyList()
    }

    fun restartPractice() {
        timerJob?.cancel()
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

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
