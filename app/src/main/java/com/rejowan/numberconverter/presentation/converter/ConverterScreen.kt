package com.rejowan.numberconverter.presentation.converter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.common.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = spacing
    var showExplanation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // Main Conversion Card
        ElevatedCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
            ) {
                // Compact base chips
                CompactBaseSelector(
                    selectedBase = uiState.fromBase,
                    onBaseSelected = { viewModel.onFromBaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Input field
                OutlinedTextField(
                    value = uiState.input,
                    onValueChange = { viewModel.onInputChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.fromBase.displayName) },
                    supportingText = {
                        if (uiState.validationError != null) {
                            Text(uiState.validationError!!)
                        }
                    },
                    isError = uiState.validationError != null,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    trailingIcon = {
                        if (uiState.input.isNotEmpty() && uiState.output.isEmpty()) {
                            IconButton(onClick = { /* TODO: Copy to clipboard */ }) {
                                Icon(Icons.Default.ContentCopy, "Copy")
                            }
                        }
                    }
                )

                // Swap button
                IconButton(
                    onClick = { viewModel.swapBases() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = "Swap bases",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Compact base chips
                CompactBaseSelector(
                    selectedBase = uiState.toBase,
                    onBaseSelected = { viewModel.onToBaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Output field (also editable)
                OutlinedTextField(
                    value = uiState.output,
                    onValueChange = { viewModel.onOutputChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.toBase.displayName) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    trailingIcon = {
                        if (uiState.output.isNotEmpty() && uiState.input.isEmpty()) {
                            IconButton(onClick = { /* TODO: Copy to clipboard */ }) {
                                Icon(Icons.Default.ContentCopy, "Copy")
                            }
                        }
                    }
                )
            }
        }

        // Action buttons
        if (uiState.output.isNotEmpty() && uiState.validationError == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                OutlinedButton(
                    onClick = { showExplanation = !showExplanation },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.small))
                    Text(if (showExplanation) "Hide Steps" else "Show Steps")
                }
            }
        }

        // Explanation Section
        if (showExplanation && uiState.explanation != null) {
            ExplanationCard(
                explanation = uiState.explanation!!,
                onDismiss = { showExplanation = false }
            )
        }

        // Error message
        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier.padding(spacing.medium),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun CompactBaseSelector(
    selectedBase: NumberBase,
    onBaseSelected: (NumberBase) -> Unit,
    modifier: Modifier = Modifier
) {
    val bases = listOf(
        NumberBase.BINARY to "BIN",
        NumberBase.OCTAL to "OCT",
        NumberBase.DECIMAL to "DEC",
        NumberBase.HEXADECIMAL to "HEX"
    )

    Row(
        modifier = modifier.wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
    ) {
        bases.forEach { (base, label) ->
            FilterChip(
                selected = selectedBase == base,
                onClick = { onBaseSelected(base) },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier.height(28.dp)
            )
        }
    }
}

@Composable
private fun ExplanationCard(
    explanation: com.rejowan.numberconverter.domain.model.Explanation,
    onDismiss: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Step-by-Step Explanation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(spacing.medium))

            // Summary
            Text(
                text = explanation.summary,
                style = MaterialTheme.typography.bodyMedium
            )

            // Integral Part
            explanation.integralPart?.let { integral ->
                Spacer(modifier = Modifier.height(spacing.medium))
                Divider()
                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    text = integral.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(spacing.small))

                integral.steps.forEach { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.small)
                    ) {
                        Text(
                            text = "Step ${step.stepNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    text = integral.result,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Fractional Part
            explanation.fractionalPart?.let { fractional ->
                Spacer(modifier = Modifier.height(spacing.medium))
                Divider()
                Spacer(modifier = Modifier.height(spacing.medium))

                Text(
                    text = fractional.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(spacing.small))

                fractional.steps.forEach { step ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.small)
                    ) {
                        Text(
                            text = "Step ${step.stepNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = step.description,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    text = fractional.result,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
