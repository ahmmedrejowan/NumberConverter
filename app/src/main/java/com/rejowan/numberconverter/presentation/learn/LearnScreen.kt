package com.rejowan.numberconverter.presentation.learn

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rejowan.numberconverter.domain.model.LessonCategory
import com.rejowan.numberconverter.domain.usecase.learn.LessonWithProgress
import com.rejowan.numberconverter.presentation.common.theme.spacing
import com.rejowan.numberconverter.presentation.learn.components.LessonCard
import com.rejowan.numberconverter.presentation.learn.components.ProgressDashboard
import com.rejowan.numberconverter.presentation.learn.state.LearnUiState
import com.rejowan.numberconverter.presentation.settings.components.SectionHeader
import org.koin.androidx.compose.koinViewModel

@Composable
fun LearnScreen(
    onLessonClick: (String) -> Unit,
    viewModel: LearnViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = spacing

    when (val state = uiState) {
        is LearnUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is LearnUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error: ${state.message}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
        is LearnUiState.Success -> {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                // Progress Dashboard
                item {
                    ProgressDashboard(
                        progressSummary = state.progressSummary,
                        modifier = Modifier.padding(horizontal = spacing.medium)
                    )
                }

                // Beginner Lessons
                state.lessonsByCategory[LessonCategory.BEGINNER]?.let { lessons ->
                    if (lessons.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Beginner")
                        }
                        items(lessons) { lessonWithProgress ->
                            LessonCard(
                                lessonWithProgress = lessonWithProgress,
                                onClick = { onLessonClick(lessonWithProgress.lesson.id) },
                                modifier = Modifier.padding(horizontal = spacing.medium)
                            )
                        }
                    }
                }

                // Intermediate Lessons
                state.lessonsByCategory[LessonCategory.INTERMEDIATE]?.let { lessons ->
                    if (lessons.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Intermediate")
                        }
                        items(lessons) { lessonWithProgress ->
                            LessonCard(
                                lessonWithProgress = lessonWithProgress,
                                onClick = { onLessonClick(lessonWithProgress.lesson.id) },
                                modifier = Modifier.padding(horizontal = spacing.medium)
                            )
                        }
                    }
                }

                // Advanced Lessons
                state.lessonsByCategory[LessonCategory.ADVANCED]?.let { lessons ->
                    if (lessons.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Advanced")
                        }
                        items(lessons) { lessonWithProgress ->
                            LessonCard(
                                lessonWithProgress = lessonWithProgress,
                                onClick = { onLessonClick(lessonWithProgress.lesson.id) },
                                modifier = Modifier.padding(horizontal = spacing.medium)
                            )
                        }
                    }
                }
            }
        }
    }
}
