package com.rejowan.numberconverter.presentation.converter.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rejowan.numberconverter.domain.model.Explanation
import com.rejowan.numberconverter.domain.model.ExplanationPart
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Step

/**
 * Bottom sheet that walks the user through a conversion. Layout:
 *  1. Conversion pill — what's being converted, at a glance
 *  2. Integral steps (primary-tinted) and/or fractional steps (tertiary-tinted)
 *  3. Final result hero — primaryContainer wrap-up with a check
 *
 * Each step shows: numbered chip, description, optional calculation,
 * optional per-step result chip.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplanationSheet(
    explanation: Explanation,
    fromBase: NumberBase,
    toBase: NumberBase,
    input: String,
    output: String,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        // Wrap the entire sheet body so users can long-press anywhere to start
        // a selection that flows across all the Text nodes (prose, math, results).
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                ConversionPill(
                    fromBase = fromBase,
                    toBase = toBase,
                    input = input,
                    output = output
                )

                // Integral part — primary-tinted
                explanation.integralPart?.let { part ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ExplanationPartSection(
                        part = part,
                        resultLabel = "Integral Part Result",
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                }

                // Fractional part — tertiary-tinted (visually distinct from integral)
                explanation.fractionalPart?.let { part ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ExplanationPartSection(
                        part = part,
                        resultLabel = "Fractional Part Result",
                        accentColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                FinalResultCard(
                    fromBase = fromBase,
                    toBase = toBase,
                    input = input,
                    output = output
                )
            }
        }
    }
}

// ============================================================================
// Top conversion pill — replaces the old verbose header + summary card.
// Shows what is being converted at a glance.
// ============================================================================

@Composable
private fun ConversionPill(
    fromBase: NumberBase,
    toBase: NumberBase,
    input: String,
    output: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConversionSide(
                base = fromBase,
                value = input,
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            // Solid accent arrow puck — punches up the divider between halves
            Surface(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .size(28.dp),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            ConversionSide(
                base = toBase,
                value = output,
                accentColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ConversionSide(
    base: NumberBase,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Surface(
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
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.ifEmpty { "—" },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

// ============================================================================
// Part section — accent-color-themed (primary for integral, tertiary for fractional)
// ============================================================================

@Composable
private fun ExplanationPartSection(
    part: ExplanationPart,
    resultLabel: String,
    accentColor: Color
) {
    Column {
        SectionHeading(title = part.title, accentColor = accentColor)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            part.steps.forEach { step ->
                StepRow(step = step, accentColor = accentColor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Part result chip
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = accentColor.copy(alpha = 0.10f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = resultLabel,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accentColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = part.result,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun SectionHeading(
    title: String,
    accentColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // 4dp × 22dp accent bar — taller for more visual weight
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

// ============================================================================
// Step row — numbered chip on the left, description + optional calculation,
// optional per-step result chip on the right. Whole row sits on a subtle
// surfaceContainerLow card for visual rhythm.
// ============================================================================

@Composable
private fun StepRow(
    step: Step,
    accentColor: Color
) {
    // The generator packs prose, calculation rows, and a trailing "Sum: X"
    // line into `description` separated by '\n', so we split it into:
    //   - the first line  → "title" (e.g. "Calculation:" / "Division steps:")
    //   - the math rows   → monospace block for alignment
    //   - the trailing "Sum:"/"Result:" line → emphasized accent line
    val parts = remember(step.description) { splitStepDescription(step.description) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 3dp accent left-edge bar — gives each step a "tagged" feel
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
            // Step number chip — solid accent for punch
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
                // Title line (prose)
                if (parts.title.isNotBlank()) {
                    Text(
                        text = parts.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Math rows in a monospace code block, only if any math lines
                if (parts.math.isNotEmpty()) {
                    if (parts.title.isNotBlank()) Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            parts.math.forEach { line ->
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

                // Trailing emphasis line (Sum: / Result: / etc.) — accent color
                parts.summary?.let { summary ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = accentColor
                    )
                }

                // Optional separate `calculation` field — almost always null in
                // practice, but rendered if the generator ever populates it.
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

/**
 * Plain-text view of the step's description, partitioned into a leading prose
 * title, indented math rows (the calculation block), and an optional trailing
 * "Sum:" / "Result:" emphasis line. Styling spans inside the AnnotatedString
 * are not preserved — the row layout reapplies emphasis where it matters.
 */
private data class StepParts(
    val title: String,
    val math: List<String>,
    val summary: String?
)

private fun splitStepDescription(description: androidx.compose.ui.text.AnnotatedString): StepParts {
    val raw = description.text
    if (raw.isEmpty()) return StepParts("", emptyList(), null)

    val lines = raw.split('\n').map { it.trimEnd() }

    // Pull out the title — the first non-empty line that isn't math/summary.
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

// ============================================================================
// Final result hero — celebratory wrap-up at the bottom of the sheet
// ============================================================================

@Composable
private fun FinalResultCard(
    fromBase: NumberBase,
    toBase: NumberBase,
    input: String,
    output: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
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

            ResultLine(label = fromBase.displayName, value = input, emphasized = false)
            Spacer(modifier = Modifier.height(8.dp))
            ResultLine(label = toBase.displayName, value = output, emphasized = true)
        }
    }
}

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
