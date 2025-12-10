package com.rejowan.numberconverter.presentation.learn.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.usecase.learn.LessonWithProgress
import com.rejowan.numberconverter.presentation.common.theme.spacing

@Composable
fun LessonCard(
    lessonWithProgress: LessonWithProgress,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = spacing

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = lessonWithProgress.lesson.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = lessonWithProgress.lesson.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${lessonWithProgress.lesson.estimatedTimeMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = spacing.extraSmall)
                    )
                }

                Spacer(modifier = Modifier.width(spacing.small))

                if (lessonWithProgress.progressPercentage >= 100f) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (lessonWithProgress.progressPercentage > 0f && lessonWithProgress.progressPercentage < 100f) {
                LinearProgressIndicator(
                    progress = { lessonWithProgress.progressPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.small)
                )
            }
        }
    }
}
