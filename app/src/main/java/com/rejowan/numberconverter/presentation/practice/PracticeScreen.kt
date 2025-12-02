package com.rejowan.numberconverter.presentation.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.presentation.common.theme.spacing
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    difficulty: Difficulty,
    onNavigateBack: () -> Unit,
    viewModel: PracticeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Practice - ${difficulty.name}") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(spacing.medium)
        ) {
            when (val state = uiState) {
                is PracticeUiState.Initial -> {
                    InitialScreen(
                        difficulty = difficulty,
                        onStartPractice = { questionCount ->
                            viewModel.startPracticeSession(difficulty, questionCount)
                        }
                    )
                }

                is PracticeUiState.Loading -> {
                    LoadingScreen()
                }

                is PracticeUiState.PracticeSession -> {
                    PracticeSessionScreen(
                        state = state,
                        onAnswerChanged = viewModel::onAnswerChanged,
                        onSubmitAnswer = viewModel::submitAnswer,
                        onNextQuestion = viewModel::nextQuestion,
                        onToggleHints = viewModel::toggleHints
                    )
                }

                is PracticeUiState.SessionComplete -> {
                    SessionCompleteScreen(
                        state = state,
                        onRetry = { viewModel.startPracticeSession(difficulty) },
                        onExit = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
private fun InitialScreen(
    difficulty: Difficulty,
    onStartPractice: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Practice Mode",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        Text(
            text = "Difficulty: ${difficulty.name}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(spacing.large))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(spacing.medium)) {
                Text(
                    text = "Choose number of questions:",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                listOf(5, 10, 15, 20).forEach { count ->
                    Button(
                        onClick = { onStartPractice(count) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("$count Questions")
                    }
                    Spacer(modifier = Modifier.height(spacing.small))
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Generating problems...")
    }
}

@Composable
private fun PracticeSessionScreen(
    state: PracticeUiState.PracticeSession,
    onAnswerChanged: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onToggleHints: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Progress indicator
        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1).toFloat() / state.totalQuestions.toFloat() },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(spacing.small))

        // Question counter and stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Question ${state.currentQuestionIndex + 1}/${state.totalQuestions}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Score: ${state.correctAnswers}/${state.currentQuestionIndex} | Streak: ${state.currentStreak}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(spacing.large))

        // Problem card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(spacing.medium)) {
                Text(
                    text = state.currentExercise.problem,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(spacing.medium))

        // Answer input
        OutlinedTextField(
            value = state.userAnswer,
            onValueChange = onAnswerChanged,
            label = { Text("Your Answer") },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.answerResult == null,
            singleLine = true
        )

        Spacer(modifier = Modifier.height(spacing.medium))

        // Hints section
        if (state.showHints && state.currentExercise.hints.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Text(
                        text = "Hints:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    state.currentExercise.hints.forEach { hint ->
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text("• $hint", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(spacing.medium))
        }

        // Answer result
        state.answerResult?.let { result ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.isCorrect)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (result.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                            contentDescription = null,
                            tint = if (result.isCorrect)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = if (result.isCorrect) "Correct!" else "Incorrect",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = spacing.small)
                        )
                    }

                    if (!result.isCorrect) {
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text("Correct answer: ${result.correctAnswer}")
                    }

                    if (state.showExplanation) {
                        Spacer(modifier = Modifier.height(spacing.small))
                        Text(
                            text = "Explanation:",
                            fontWeight = FontWeight.Bold
                        )
                        Text(result.explanation)
                    }
                }
            }
            Spacer(modifier = Modifier.height(spacing.medium))
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            if (state.answerResult == null) {
                OutlinedButton(
                    onClick = onToggleHints,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Lightbulb, null)
                    Text("Hint", modifier = Modifier.padding(start = spacing.small))
                }

                Button(
                    onClick = onSubmitAnswer,
                    modifier = Modifier.weight(1f),
                    enabled = state.userAnswer.isNotBlank()
                ) {
                    Text("Submit")
                }
            } else {
                Button(
                    onClick = onNextQuestion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next Question")
                }
            }
        }
    }
}

@Composable
private fun SessionCompleteScreen(
    state: PracticeUiState.SessionComplete,
    onRetry: () -> Unit,
    onExit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(spacing.large)
        )

        Text(
            text = "Session Complete!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(spacing.large))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${state.percentage.toInt()}%",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "${state.correctAnswers} / ${state.totalQuestions} Correct",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(spacing.medium))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${state.longestStreak}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Best Streak", style = MaterialTheme.typography.bodySmall)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${state.points}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Points", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.large))

        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Try Again")
        }

        Spacer(modifier = Modifier.height(spacing.small))

        OutlinedButton(
            onClick = onExit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exit")
        }
    }
}
