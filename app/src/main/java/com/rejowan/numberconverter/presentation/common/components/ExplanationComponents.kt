package com.rejowan.numberconverter.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.domain.model.ExplanationPart
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Step

// ============================================================================
// Shared building blocks for the Converter and Calculator explanation sheets.
// Both sheets walk the user through the same kind of work, so they render with
// the same accent bars, numbered step chips, and monospace math blocks.
// ============================================================================

/**
 * One titled block of steps, tinted by [accentColor], closed off with a result
 * chip. Callers pick the accent to distinguish parts (primary for integral /
 * first operand, tertiary for fractional / second operand, and so on).
 */
@Composable
fun ExplanationPartSection(
    part: ExplanationPart,
    resultLabel: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionHeading(title = part.title, accentColor = accentColor)

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            part.steps.forEach { step ->
                StepRow(step = step, accentColor = accentColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        ResultChip(
            label = resultLabel,
            value = part.result.text,
            accentColor = accentColor
        )
    }
}

/**
 * Accent bar + title. The bar is what ties a section to its color.
 */
@Composable
fun SectionHeading(
    title: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp),
            shape = RoundedCornerShape(2.dp),
            color = accentColor
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * Soft accent-tinted slab holding a labelled value — used to close out a
 * section and to show an operator's outcome.
 */
@Composable
fun ResultChip(
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = accentColor.copy(alpha = 0.10f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * A single step: accent left-edge, numbered chip, prose, an optional monospace
 * math block, and an optional emphasised trailing line.
 */
@Composable
fun StepRow(
    step: Step,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    // The generator packs prose, calculation rows, and a trailing "Sum: X" line
    // into `description` separated by '\n', so we split it back apart here.
    val parts = remember(step.description) { splitStepDescription(step.description) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(48.dp)
                    .background(accentColor)
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 12.dp, bottom = 12.dp, end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    modifier = Modifier.size(26.dp),
                    shape = RoundedCornerShape(7.dp),
                    color = accentColor
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = step.stepNumber.toString(),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    if (parts.title.isNotBlank()) {
                        Text(
                            text = parts.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    if (parts.math.isNotEmpty()) {
                        if (parts.title.isNotBlank()) Spacer(modifier = Modifier.height(8.dp))
                        MathBlock(lines = parts.math)
                    }

                    parts.summary?.let { summary ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = accentColor
                        )
                    }

                    step.calculation?.let { calc ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = calc,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MathBlock(
    lines: List<String>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLowest
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            lines.forEach { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 18.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Base badge — the three-letter tag (BIN / OCT / DEC / HEX) used everywhere a
 * value needs to say which base it is in.
 */
@Composable
fun BaseBadge(
    base: NumberBase,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = accentColor.copy(alpha = 0.15f)
    ) {
        Text(
            text = base.displayName.uppercase().take(3),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = accentColor,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

/**
 * Celebratory wrap-up card closing every explanation sheet.
 */
@Composable
fun FinalResultCard(
    lines: List<ResultLineData>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Final Result",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            lines.forEachIndexed { index, line ->
                if (index > 0) Spacer(modifier = Modifier.height(8.dp))
                ResultLine(label = line.label, value = line.value, emphasized = line.emphasized)
            }
        }
    }
}

data class ResultLineData(
    val label: String,
    val value: String,
    val emphasized: Boolean = false
)

@Composable
private fun ResultLine(label: String, value: String, emphasized: Boolean) {
    Column {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            style = if (emphasized) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// ============================================================================
// Step description parsing
// ============================================================================

/**
 * Plain-text view of a step's description, partitioned into a leading prose
 * title, indented math rows, and an optional trailing "Sum:" / "Result:" line.
 * Styling spans inside the AnnotatedString are not preserved — the row layout
 * reapplies emphasis where it matters.
 */
data class StepParts(
    val title: String,
    val math: List<String>,
    val summary: String?
)

fun splitStepDescription(description: AnnotatedString): StepParts {
    val raw = description.text
    if (raw.isEmpty()) return StepParts("", emptyList(), null)

    val lines = raw.split('\n').map { it.trimEnd() }

    var title = ""
    val math = mutableListOf<String>()
    var summary: String? = null

    for (line in lines) {
        val trimmed = line.trim()
        if (trimmed.isEmpty()) continue
        when {
            // Trailing emphasis lines — usually "Sum: X" or "Result: X"
            // (and also "Result (read remainders bottom to top): X")
            summary == null &&
                math.isNotEmpty() &&
                (trimmed.startsWith("Sum:", ignoreCase = true) ||
                    trimmed.startsWith("Result:", ignoreCase = true) ||
                    trimmed.startsWith("Result ", ignoreCase = true)) -> {
                summary = trimmed
            }
            // Indented (originally "  ...") math rows
            line.startsWith("  ") -> math.add(trimmed)
            // First non-indented prose line is the title; subsequent
            // non-indented prose lines append to it with a space.
            else -> {
                title = if (title.isEmpty()) trimmed else "$title $trimmed"
            }
        }
    }

    return StepParts(title = title, math = math, summary = summary)
}
