package com.rejowan.numberconverter.presentation.practice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.presentation.common.theme.spacing
import com.rejowan.numberconverter.presentation.settings.components.SectionHeader
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSessionScreen(
    practiceType: String,
    onNavigateBack: () -> Unit,
    viewModel: PracticeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Set the practice type on launch
    LaunchedEffect(practiceType) {
        val type = when (practiceType) {
            "conversion" -> PracticeType.CONVERSION
            "calculation" -> PracticeType.CALCULATION
            "mcq" -> PracticeType.MCQ
            else -> PracticeType.CONVERSION
        }
        viewModel.selectPracticeType(type)
    }

    val title = when (val state = uiState) {
        is PracticeUiState.Setup -> "${state.practiceType.displayName} Practice"
        is PracticeUiState.Quiz -> "Question ${state.currentQuestionIndex + 1}/${state.totalQuestions}"
        is PracticeUiState.Complete -> "Results"
        else -> "Practice"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
                is PracticeUiState.SelectType -> {
                    // Should not reach here, but just in case
                    LoadingContent()
                }
                is PracticeUiState.Setup -> SetupContent(
                    state = state,
                    onDifficultyChanged = viewModel::updateDifficulty,
                    onQuestionCountChanged = viewModel::updateQuestionCount,
                    onStartPractice = viewModel::startPractice
                )
                is PracticeUiState.Loading -> LoadingContent()
                is PracticeUiState.Quiz -> QuizContent(
                    state = state,
                    onAnswerChanged = viewModel::onAnswerChanged,
                    onSubmitAnswer = viewModel::submitAnswer,
                    onNextQuestion = viewModel::nextQuestion,
                    onToggleHints = viewModel::toggleHints
                )
                is PracticeUiState.Complete -> CompleteContent(
                    state = state,
                    onRestart = viewModel::restartPractice,
                    onBackToMenu = onNavigateBack
                )
            }
        }
    }
}

@Composable
private fun SetupContent(
    state: PracticeUiState.Setup,
    onDifficultyChanged: (Difficulty) -> Unit,
    onQuestionCountChanged: (Int) -> Unit,
    onStartPractice: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        item {
            SectionHeader(title = "Settings")
        }

        // Difficulty Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Text(
                        text = "Difficulty",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(spacing.small))

                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                        Difficulty.entries.forEachIndexed { index, difficulty ->
                            SegmentedButton(
                                selected = state.selectedDifficulty == difficulty,
                                onClick = { onDifficultyChanged(difficulty) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = Difficulty.entries.size
                                )
                            ) {
                                Text(difficulty.displayName)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.small))

                    Text(
                        text = when (state.selectedDifficulty) {
                            Difficulty.EASY -> "Small numbers (1-15), more hints available"
                            Difficulty.MEDIUM -> "Medium numbers (16-255), some hints"
                            Difficulty.HARD -> "Large numbers (256-4095), minimal hints"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Question Count Selection
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(spacing.medium)) {
                    Text(
                        text = "Number of Questions",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(spacing.small))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        listOf(5, 10, 15, 20).forEach { count ->
                            FilterChip(
                                selected = state.selectedQuestionCount == count,
                                onClick = { onQuestionCountChanged(count) },
                                label = { Text("$count") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.medium),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Spacer(modifier = Modifier.width(spacing.medium))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ready to Practice",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${state.selectedQuestionCount} ${state.selectedDifficulty.displayName} questions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(spacing.small))
        }

        // Start Button
        item {
            Button(
                onClick = onStartPractice,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(spacing.small))
                Text("Start Practice")
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(spacing.medium))
            Text(
                text = "Generating questions...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuizContent(
    state: PracticeUiState.Quiz,
    onAnswerChanged: (String) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onToggleHints: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // Progress Header
        item {
            Column(modifier = Modifier.padding(horizontal = spacing.medium)) {
                LinearProgressIndicator(
                    progress = { (state.currentQuestionIndex + 1).toFloat() / state.totalQuestions.toFloat() },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(spacing.small))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${state.correctAnswers} correct",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (state.currentStreak > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${state.currentStreak}",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (state.currentStreak > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Problem Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = state.currentExercise.problem,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Answer Input - MCQ options or text field
        if (state.currentExercise.options.isNotEmpty()) {
            // MCQ Options
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.small)
                ) {
                    state.currentExercise.options.forEachIndexed { index, option ->
                        val optionLabel = ('A' + index).toString()
                        val isSelected = state.userAnswer == option
                        val isAnswered = state.answerResult != null
                        val isCorrect = option == state.currentExercise.correctAnswer

                        val containerColor = when {
                            isAnswered && isCorrect -> MaterialTheme.colorScheme.primaryContainer
                            isAnswered && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                            isSelected -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (!isAnswered) {
                                    onAnswerChanged(option)
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing.medium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$optionLabel.",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(spacing.medium))
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isAnswered && isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                } else if (isAnswered && isSelected && !isCorrect) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Incorrect",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Text Input for non-MCQ
            item {
                OutlinedTextField(
                    value = state.userAnswer,
                    onValueChange = onAnswerChanged,
                    label = { Text("Your Answer") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
                    enabled = state.answerResult == null,
                    singleLine = true
                )
            }
        }

        // Hints
        if (state.showHints && state.currentExercise.hints.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(spacing.medium)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(spacing.small))
                            Text(
                                text = "Hints",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(spacing.small))
                        state.currentExercise.hints.forEach { hint ->
                            Text(
                                text = "• $hint",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }

        // Answer Result
        state.answerResult?.let { result ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.medium),
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
                            Spacer(modifier = Modifier.width(spacing.small))
                            Text(
                                text = if (result.isCorrect) "Correct!" else "Incorrect",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (!result.isCorrect) {
                            Spacer(modifier = Modifier.height(spacing.small))
                            Text(
                                text = "Correct answer: ${result.correctAnswer}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(spacing.small))
                        Text(
                            text = result.explanation,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Action Buttons
        item {
            Column(
                modifier = Modifier.padding(horizontal = spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                val isMcq = state.currentExercise.options.isNotEmpty()

                if (state.answerResult == null) {
                    if (isMcq) {
                        // MCQ mode - just show hint and submit buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            OutlinedButton(
                                onClick = onToggleHints,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(spacing.extraSmall))
                                Text(if (state.showHints) "Hide" else "Hint")
                            }

                            Button(
                                onClick = onSubmitAnswer,
                                modifier = Modifier.weight(1f),
                                enabled = state.userAnswer.isNotBlank()
                            ) {
                                Text("Confirm")
                            }
                        }
                    } else {
                        // Text input mode
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(spacing.small)
                        ) {
                            OutlinedButton(
                                onClick = onToggleHints,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(spacing.extraSmall))
                                Text(if (state.showHints) "Hide" else "Hint")
                            }

                            Button(
                                onClick = onSubmitAnswer,
                                modifier = Modifier.weight(1f),
                                enabled = state.userAnswer.isNotBlank()
                            ) {
                                Text("Submit")
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            if (state.currentQuestionIndex + 1 < state.totalQuestions)
                                "Next Question"
                            else
                                "See Results"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CompleteContent(
    state: PracticeUiState.Complete,
    onRestart: () -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // Main Score Card
        item {
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.large),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = when {
                            state.percentage >= 80 -> MaterialTheme.colorScheme.primary
                            state.percentage >= 60 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )

                    Spacer(modifier = Modifier.height(spacing.small))

                    Text(
                        text = "${state.percentage.toInt()}%",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            state.percentage >= 80 -> MaterialTheme.colorScheme.primary
                            state.percentage >= 60 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )

                    Text(
                        text = "${state.correctAnswers} / ${state.totalQuestions} Correct",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(spacing.extraSmall))

                    Text(
                        text = "${state.practiceType.displayName} • ${state.difficulty.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            SectionHeader(title = "Stats")
        }

        // Stats Cards
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.medium),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                StatCard(
                    label = "Best Streak",
                    value = "${state.longestStreak}",
                    icon = Icons.Default.LocalFireDepartment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Points",
                    value = "${state.points}",
                    icon = Icons.Default.Star,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(spacing.small))
        }

        // Action Buttons
        item {
            Column(
                modifier = Modifier.padding(horizontal = spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text("Practice Again")
                }

                OutlinedButton(
                    onClick = onBackToMenu,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Back to Menu")
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
