package com.rejowan.numberconverter.presentation.calculator.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.rejowan.numberconverter.domain.model.CalculatorExplanation
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import com.rejowan.numberconverter.presentation.common.components.BaseBadge
import com.rejowan.numberconverter.presentation.common.components.ExplanationPartSection
import com.rejowan.numberconverter.presentation.common.components.FinalResultCard
import com.rejowan.numberconverter.presentation.common.components.ResultChip
import com.rejowan.numberconverter.presentation.common.components.ResultLineData
import com.rejowan.numberconverter.presentation.common.components.SectionHeading

/**
 * Bottom sheet that walks the user through a calculation — the Calculator's
 * counterpart to the Converter's `ExplanationSheet`, built from the same
 * shared step components so the two read identically.
 *
 * Layout:
 *  1. Equation pill — `a ⊕ b = c` with base badges, at a glance
 *  2. Operand conversions to decimal (primary / tertiary tinted), when needed
 *  3. The arithmetic itself (secondary tinted)
 *  4. Result conversion back to the output base, when needed
 *  5. Final result hero
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorExplanationSheet(
    explanation: CalculatorExplanation,
    input1: String,
    input1Base: NumberBase,
    input2: String,
    input2Base: NumberBase,
    operation: Operation,
    output: String,
    outputBase: NumberBase,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EquationPill(
                    input1 = input1,
                    input1Base = input1Base,
                    input2 = input2,
                    input2Base = input2Base,
                    operation = operation,
                    output = output,
                    outputBase = outputBase
                )

                // Operand conversions — only present when the operand isn't
                // already decimal, since the arithmetic happens in base 10.
                explanation.input1Conversion?.let { part ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ExplanationPartSection(
                        part = part,
                        resultLabel = "First Number in Decimal",
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                }

                explanation.input2Conversion?.let { part ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ExplanationPartSection(
                        part = part,
                        resultLabel = "Second Number in Decimal",
                        accentColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                // The arithmetic step itself
                Spacer(modifier = Modifier.height(20.dp))
                OperationSection(
                    title = explanation.operation.title,
                    description = explanation.operation.description.text,
                    result = explanation.operation.result
                )

                // Converting the decimal result back into the requested base
                explanation.outputConversion?.let { part ->
                    Spacer(modifier = Modifier.height(20.dp))
                    ExplanationPartSection(
                        part = part,
                        resultLabel = "Result in ${outputBase.displayName}",
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                FinalResultCard(
                    lines = listOf(
                        ResultLineData(
                            label = "${input1Base.displayName} ${operation.symbol} ${input2Base.displayName}",
                            value = "$input1 ${operation.symbol} $input2"
                        ),
                        ResultLineData(
                            label = outputBase.displayName,
                            value = output,
                            emphasized = true
                        )
                    )
                )
            }
        }
    }
}

// ============================================================================
// Equation pill — the calculator's analogue of the converter's conversion pill
// ============================================================================

@Composable
private fun EquationPill(
    input1: String,
    input1Base: NumberBase,
    input2: String,
    input2Base: NumberBase,
    operation: Operation,
    output: String,
    outputBase: NumberBase
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                OperandSide(
                    base = input1Base,
                    value = input1,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                OperatorPuck(symbol = operation.symbol)
                OperandSide(
                    base = input2Base,
                    value = input2,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "=",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OperandSide(
                    base = outputBase,
                    value = output,
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OperatorPuck(symbol: String) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .size(28.dp),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun OperandSide(
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

// ============================================================================
// Operation section — prose describing the arithmetic, plus its outcome
// ============================================================================

@Composable
private fun OperationSection(
    title: String,
    description: String,
    result: String
) {
    // Primary rather than secondary — in the violet scheme `secondary` is a
    // muted grey that makes the sheet's centrepiece look washed out next to the
    // operand sections.
    val accent = MaterialTheme.colorScheme.primary

    Column {
        SectionHeading(title = title, accentColor = accent)

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(14.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ResultChip(
            label = "Decimal Result",
            value = result,
            accentColor = accent
        )
    }
}
