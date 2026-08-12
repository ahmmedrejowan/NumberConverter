package com.rejowan.numberconverter.presentation.converter.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.Explanation
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.common.components.BaseBadge
import com.rejowan.numberconverter.presentation.common.components.ExplanationPartSection
import com.rejowan.numberconverter.presentation.common.components.FinalResultCard
import com.rejowan.numberconverter.presentation.common.components.ResultLineData

/**
 * Bottom sheet that walks the user through a conversion. Layout:
 *  1. Conversion pill — what's being converted, at a glance
 *  2. Integral steps (primary-tinted) and/or fractional steps (tertiary-tinted)
 *  3. Final result hero — primaryContainer wrap-up with a check
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
                    lines = listOf(
                        ResultLineData(fromBase.displayName, input),
                        ResultLineData(toBase.displayName, output, emphasized = true)
                    )
                )
            }
        }
    }
}

// ============================================================================
// Top conversion pill — shows what is being converted at a glance.
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
        BaseBadge(base = base, accentColor = accentColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value.ifEmpty { "—" },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
