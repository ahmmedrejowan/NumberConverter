package com.rejowan.numberconverter.presentation.lesson

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.domain.model.Exercise
import com.rejowan.numberconverter.domain.model.LessonSection
import com.rejowan.numberconverter.domain.model.Question
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lessonId: String,
    onNavigateBack: () -> Unit,
    viewModel: LessonDetailViewModel = koinViewModel { parametersOf(lessonId) }
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation back when lesson is finished
    LaunchedEffect(uiState) {
        if (uiState is LessonDetailUiState.Success && (uiState as LessonDetailUiState.Success).shouldNavigateBack) {
            onNavigateBack()
            viewModel.onNavigatedBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is LessonDetailUiState.Success -> Text(text = state.lesson.title)
                        else -> Text(text = stringResource(R.string.lesson))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is LessonDetailUiState.Success -> {
                            if (!state.isLastSection && state.canProceedToNext) {
                                IconButton(onClick = viewModel::nextSection) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Next section"
                                    )
                                }
                            }
                        }
                        else -> {}
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is LessonDetailUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is LessonDetailUiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        onRetry = viewModel::retry,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is LessonDetailUiState.Success -> {
                    LessonContent(
                        state = state,
                        onMarkSectionComplete = viewModel::markSectionComplete,
                        onNextSection = viewModel::nextSection,
                        onPreviousSection = viewModel::previousSection,
                        onSubmitQuiz = viewModel::submitQuizAnswers,
                        onRetryQuiz = viewModel::retryQuiz,
                        onFinishLesson = viewModel::finishLesson
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
private fun LessonContent(
    state: LessonDetailUiState.Success,
    onMarkSectionComplete: (String, Boolean) -> Unit,
    onNextSection: () -> Unit,
    onPreviousSection: () -> Unit,
    onSubmitQuiz: (Map<String, Any>) -> Unit,
    onRetryQuiz: () -> Unit,
    onFinishLesson: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { state.overallProgress },
            modifier = Modifier.fillMaxWidth(),
        )

        // Section content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (val section = state.currentSection) {
                is LessonSection.Theory -> {
                    TheorySection(
                        section = section,
                        isCompleted = section.id in state.completedSections,
                        onMarkComplete = { onMarkSectionComplete(section.id, false) }
                    )
                }

                is LessonSection.Interactive -> {
                    InteractiveSection(
                        section = section,
                        isCompleted = section.id in state.completedSections,
                        onMarkComplete = { onMarkSectionComplete(section.id, true) }
                    )
                }

                is LessonSection.Practice -> {
                    PracticeSection(
                        section = section,
                        isCompleted = section.id in state.completedSections,
                        onMarkComplete = { onMarkSectionComplete(section.id, true) }
                    )
                }

                is LessonSection.Quiz -> {
                    QuizSection(
                        section = section,
                        showResults = state.showQuizResults,
                        score = state.quizScore,
                        userAnswers = state.quizAnswers,
                        onSubmitAnswers = onSubmitQuiz,
                        onRetry = onRetryQuiz
                    )
                }
            }
        }

        // Navigation buttons or Finish button
        if (state.isLastSection && state.allSectionsCompleted) {
            FinishButton(onFinish = onFinishLesson)
        } else {
            NavigationButtons(
                state = state,
                onPrevious = onPreviousSection,
                onNext = onNextSection
            )
        }
    }
}

@Composable
private fun TheorySection(
    section: LessonSection.Theory,
    isCompleted: Boolean,
    onMarkComplete: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = section.content,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!isCompleted) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onMarkComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Mark as Complete")
            }
        } else {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Section Completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun InteractiveSection(
    section: LessonSection.Interactive,
    isCompleted: Boolean,
    onMarkComplete: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var userAnswer by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }
    var currentHintIndex by remember { mutableStateOf(0) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = section.description,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Exercise",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = section.exercise.problem,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it; isCorrect = null },
            label = { Text("Your Answer") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isCompleted,
            isError = isCorrect == false
        )

        Spacer(modifier = Modifier.height(8.dp))

        AnimatedVisibility(visible = isCorrect == true) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Correct! ${section.exercise.explanation ?: ""}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        AnimatedVisibility(visible = isCorrect == false) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Not quite right. Try again!",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isCompleted && isCorrect != true) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (section.hints.isNotEmpty()) {
                    OutlinedButton(
                        onClick = {
                            showHint = true
                            if (currentHintIndex < section.hints.size - 1) {
                                currentHintIndex++
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Show Hint")
                    }
                }

                Button(
                    onClick = {
                        val correct = userAnswer.trim().equals(section.exercise.correctAnswer, ignoreCase = true)
                        isCorrect = correct
                        if (correct) {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            onMarkComplete()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Check Answer")
                }
            }
        } else if (isCompleted) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Section Completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showHint && section.hints.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Hint ${currentHintIndex}:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = section.hints[currentHintIndex - 1],
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun PracticeSection(
    section: LessonSection.Practice,
    isCompleted: Boolean,
    onMarkComplete: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val exerciseAnswers = remember { mutableStateMapOf<String, String>() }
    val exerciseResults = remember { mutableStateMapOf<String, Boolean>() }
    val allCorrect = remember(exerciseResults) {
        section.exercises.all { exerciseResults[it.id] == true }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        section.exercises.forEachIndexed { index, exercise ->
            PracticeExerciseItem(
                exercise = exercise,
                index = index + 1,
                userAnswer = exerciseAnswers[exercise.id] ?: "",
                onAnswerChange = { exerciseAnswers[exercise.id] = it; exerciseResults.remove(exercise.id) },
                result = exerciseResults[exercise.id],
                onCheckAnswer = {
                    val isCorrect = exerciseAnswers[exercise.id]?.trim()?.equals(exercise.correctAnswer, ignoreCase = true) == true
                    exerciseResults[exercise.id] = isCorrect
                    if (isCorrect) {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
                enabled = !isCompleted
            )

            if (index < section.exercises.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!isCompleted && allCorrect) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onMarkComplete()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Complete Section")
            }
        } else if (isCompleted) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Section Completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun PracticeExerciseItem(
    exercise: Exercise,
    index: Int,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    result: Boolean?,
    onCheckAnswer: () -> Unit,
    enabled: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Exercise $index",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exercise.problem,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = userAnswer,
                onValueChange = onAnswerChange,
                label = { Text("Your Answer") },
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled && result != true,
                isError = result == false
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (result) {
                true -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Correct!${exercise.explanation?.let { " $it" } ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                false -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Try again",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                null -> {
                    if (enabled) {
                        Button(
                            onClick = onCheckAnswer,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Check Answer")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizSection(
    section: LessonSection.Quiz,
    showResults: Boolean,
    score: Int?,
    userAnswers: Map<String, Any>,
    onSubmitAnswers: (Map<String, Any>) -> Unit,
    onRetry: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val answers = remember { mutableStateMapOf<String, Any>() }
    val shownHints = remember { mutableStateMapOf<String, Int>() } // Track how many hints shown per question

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = section.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (showResults && score != null) {
            QuizResults(
                score = score,
                totalQuestions = section.questions.size,
                onRetry = onRetry
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        section.questions.forEachIndexed { index, question ->
            QuestionItem(
                question = question,
                index = index + 1,
                userAnswer = if (showResults) userAnswers[question.id] else answers[question.id],
                onAnswerSelected = { answers[question.id] = it },
                showResult = showResults,
                shownHintsCount = shownHints[question.id] ?: 0,
                onShowHint = {
                    val currentCount = shownHints[question.id] ?: 0
                    if (currentCount < question.hints.size) {
                        shownHints[question.id] = currentCount + 1
                    }
                }
            )

            if (index < section.questions.size - 1) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!showResults) {
            Button(
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    onSubmitAnswers(answers.toMap())
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = answers.size == section.questions.size
            ) {
                Text(text = "Submit Quiz")
            }
        }
    }
}

@Composable
private fun QuizResults(
    score: Int,
    totalQuestions: Int,
    onRetry: () -> Unit
) {
    val passed = score >= 70

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (passed) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.errorContainer
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = if (passed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Score: $score%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (passed) "Congratulations! You passed!" else "Keep trying! You need 70% to pass.",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!passed) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onRetry) {
                    Text(text = "Retry Quiz")
                }
            }
        }
    }
}

@Composable
private fun QuestionItem(
    question: Question,
    index: Int,
    userAnswer: Any?,
    onAnswerSelected: (Any) -> Unit,
    showResult: Boolean,
    shownHintsCount: Int = 0,
    onShowHint: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $index",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                // Show hint button if hints available and not showing results
                if (!showResult && question.hints.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onShowHint,
                        enabled = shownHintsCount < question.hints.size,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(
                            text = if (shownHintsCount < question.hints.size) {
                                "Hint (${shownHintsCount}/${question.hints.size})"
                            } else {
                                "No more hints"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyMedium
            )

            // Show hints if any have been revealed
            if (shownHintsCount > 0 && question.hints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                question.hints.take(shownHintsCount).forEachIndexed { hintIndex, hint ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "💡",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = "Hint ${hintIndex + 1}:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = hint,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                    if (hintIndex < shownHintsCount - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (question) {
                is Question.MultipleChoice -> {
                    MultipleChoiceAnswers(
                        options = question.options,
                        correctIndex = question.correctAnswerIndex,
                        selectedIndex = userAnswer as? Int,
                        onSelect = onAnswerSelected,
                        showResult = showResult
                    )
                }
                is Question.FillBlank -> {
                    FillBlankAnswer(
                        userAnswer = userAnswer as? String ?: "",
                        correctAnswer = question.correctAnswer,
                        onAnswerChange = onAnswerSelected,
                        showResult = showResult
                    )
                }
                is Question.TrueFalse -> {
                    TrueFalseAnswers(
                        correctAnswer = question.correctAnswer,
                        selectedAnswer = userAnswer as? Boolean,
                        onSelect = onAnswerSelected,
                        showResult = showResult
                    )
                }
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.explanation,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MultipleChoiceAnswers(
    options: List<String>,
    correctIndex: Int,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    showResult: Boolean
) {
    options.forEachIndexed { index, option ->
        val isSelected = selectedIndex == index
        val isCorrect = index == correctIndex
        val showCorrect = showResult && isCorrect
        val showIncorrect = showResult && isSelected && !isCorrect

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectable(
                    selected = isSelected,
                    onClick = { if (!showResult) onSelect(index) },
                    enabled = !showResult
                )
                .background(
                    when {
                        showCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        showIncorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surface
                    },
                    RoundedCornerShape(8.dp)
                )
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = { if (!showResult) onSelect(index) },
                enabled = !showResult
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = option,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (showCorrect) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            } else if (showIncorrect) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        if (index < options.size - 1) {
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun FillBlankAnswer(
    userAnswer: String,
    correctAnswer: String,
    onAnswerChange: (String) -> Unit,
    showResult: Boolean
) {
    OutlinedTextField(
        value = userAnswer,
        onValueChange = { onAnswerChange(it) },
        label = { Text("Your Answer") },
        modifier = Modifier.fillMaxWidth(),
        enabled = !showResult,
        isError = showResult && userAnswer.trim().lowercase() != correctAnswer.lowercase()
    )

    if (showResult && userAnswer.trim().lowercase() != correctAnswer.lowercase()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Correct answer: $correctAnswer",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TrueFalseAnswers(
    correctAnswer: Boolean,
    selectedAnswer: Boolean?,
    onSelect: (Boolean) -> Unit,
    showResult: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(true to "True", false to "False").forEach { (value, label) ->
            val isSelected = selectedAnswer == value
            val isCorrect = value == correctAnswer
            val showCorrect = showResult && isCorrect
            val showIncorrect = showResult && isSelected && !isCorrect

            OutlinedButton(
                onClick = { if (!showResult) onSelect(value) },
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                    containerColor = when {
                        showCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        showIncorrect -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                enabled = !showResult
            ) {
                Text(text = label)
                if (showCorrect) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                } else if (showIncorrect) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavigationButtons(
    state: LessonDetailUiState.Success,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            if (!state.isFirstSection) {
                OutlinedButton(
                    onClick = onPrevious,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Previous")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Progress indicator
            Text(
                text = "${state.currentSectionIndex + 1} / ${state.lesson.sections.size}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Next button
            if (!state.isLastSection) {
                Button(
                    onClick = onNext,
                    enabled = state.canProceedToNext,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Next")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun FinishButton(onFinish: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Congratulations!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "You've completed this lesson",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onFinish,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Finish")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
