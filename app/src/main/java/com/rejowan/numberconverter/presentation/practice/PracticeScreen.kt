package com.rejowan.numberconverter.presentation.practice

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.presentation.common.theme.spacing
import com.rejowan.numberconverter.presentation.settings.components.SectionHeader

@Composable
fun PracticeScreen(
    onNavigateToPracticeSession: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // Practice Mode Section
        item {
            SectionHeader(title = "Practice Mode")
        }

        item {
            PracticeTypeCard(
                title = "Conversion Practice",
                description = "Convert numbers between Binary, Octal, Decimal, and Hexadecimal",
                icon = Icons.Default.SwapHoriz,
                onClick = { onNavigateToPracticeSession("conversion") },
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }

        item {
            PracticeTypeCard(
                title = "Calculation Practice",
                description = "Perform arithmetic operations with numbers in different bases",
                icon = Icons.Default.Calculate,
                onClick = { onNavigateToPracticeSession("calculation") },
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }

        item {
            PracticeTypeCard(
                title = "Multiple Choice Quiz",
                description = "Test your knowledge with multiple choice questions on conversions and calculations",
                icon = Icons.Default.Quiz,
                onClick = { onNavigateToPracticeSession("mcq") },
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }

        item {
            PracticeTypeCard(
                title = "Timed Exam",
                description = "Challenge yourself with a timed exam - complete as many questions as you can",
                icon = Icons.Default.Timer,
                onClick = { onNavigateToPracticeSession("exam") },
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }

        // Tips Section
        item {
            Spacer(modifier = Modifier.height(spacing.small))
        }

        item {
            SectionHeader(title = "Tips")
        }

        item {
            TipCard(
                title = "Start with Easy",
                description = "Begin with easy difficulty to build your understanding of number bases",
                icon = Icons.Default.Star,
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }

        item {
            TipCard(
                title = "Use Hints",
                description = "Don't hesitate to use hints - they help you learn the conversion steps",
                icon = Icons.Default.Lightbulb,
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }

        item {
            TipCard(
                title = "Build Streaks",
                description = "Answering correctly in a row earns bonus points",
                icon = Icons.Default.LocalFireDepartment,
                modifier = Modifier.padding(horizontal = spacing.medium)
            )
        }
    }
}

@Composable
private fun PracticeTypeCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TipCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.tertiary
            )

            Spacer(modifier = Modifier.width(spacing.medium))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
