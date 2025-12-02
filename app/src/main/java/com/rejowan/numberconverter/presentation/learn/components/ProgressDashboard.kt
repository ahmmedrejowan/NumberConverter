package com.rejowan.numberconverter.presentation.learn.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rejowan.numberconverter.domain.usecase.learn.ProgressSummary
import com.rejowan.numberconverter.presentation.common.theme.spacing

@Composable
fun ProgressDashboard(
    progressSummary: ProgressSummary,
    modifier: Modifier = Modifier
) {
    val spacing = spacing

    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium)
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "${progressSummary.completedLessons} of ${progressSummary.totalLessons} lessons completed",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = spacing.small)
            )

            LinearProgressIndicator(
                progress = { progressSummary.overallProgress / 100f },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${progressSummary.overallProgress.toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = spacing.extraSmall)
            )
        }
    }
}
