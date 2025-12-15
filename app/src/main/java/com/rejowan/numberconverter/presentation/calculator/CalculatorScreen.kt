package com.rejowan.numberconverter.presentation.calculator

import android.content.Intent
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rejowan.numberconverter.domain.model.CalculatorExplanation
import com.rejowan.numberconverter.domain.model.ExplanationPart
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation
import com.rejowan.numberconverter.presentation.common.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = spacing
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    var showExplanation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // Main Calculator Card
        ElevatedCard(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth()
                .clickable { focusManager.clearFocus() }
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
            ) {
                // Input 1 Section
                Text(
                    text = "First Number",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))

                CompactBaseSelector(
                    selectedBase = uiState.input1Base,
                    onBaseSelected = { viewModel.onInput1BaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = uiState.input1,
                    onValueChange = { viewModel.onInput1Changed(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.input1Base.displayName) },
                    placeholder = { Text("Enter value") },
                    supportingText = if (uiState.validation1Error != null) {
                        { Text(uiState.validation1Error!!) }
                    } else null,
                    isError = uiState.validation1Error != null,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = getKeyboardTypeForBase(uiState.input1Base)
                    )
                )


                // Operation Selector with Swap Button
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OperationSelector(
                        selectedOperation = uiState.operation,
                        onOperationSelected = { viewModel.onOperationChanged(it) }
                    )

                    Spacer(Modifier.width(12.dp))

                    IconButton(
                        onClick = { viewModel.swapInputs() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .semantics {
                                contentDescription = "Swap first and second numbers"
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                // Input 2 Section
                Text(
                    text = "Second Number",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))

                CompactBaseSelector(
                    selectedBase = uiState.input2Base,
                    onBaseSelected = { viewModel.onInput2BaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = uiState.input2,
                    onValueChange = { viewModel.onInput2Changed(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.input2Base.displayName) },
                    placeholder = { Text("Enter value") },
                    supportingText = if (uiState.validation2Error != null) {
                        { Text(uiState.validation2Error!!) }
                    } else null,
                    isError = uiState.validation2Error != null,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = getKeyboardTypeForBase(uiState.input2Base)
                    )
                )

                Spacer(Modifier.height(12.dp))

                // Output Section
                Text(
                    text = "Result",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))

                CompactBaseSelector(
                    selectedBase = uiState.outputBase,
                    onBaseSelected = { viewModel.onOutputBaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                OutlinedTextField(
                    value = uiState.output,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.outputBase.displayName) },
                    placeholder = { Text("Result") },
                    readOnly = true,
                    singleLine = false,
                    textStyle = MaterialTheme.typography.titleMedium
                )

                // Quick action icons
                if (uiState.output.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                        ) {
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(uiState.output))
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.ContentCopy,
                                    contentDescription = "Copy result to clipboard",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = { viewModel.clearAll() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Clear all inputs",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val shareText = buildString {
                                        append("${uiState.input1Base.displayName}: ${uiState.input1}")
                                        append(" ${uiState.operation.symbol} ")
                                        append("${uiState.input2Base.displayName}: ${uiState.input2}")
                                        append(" = ")
                                        append("${uiState.outputBase.displayName}: ${uiState.output}")
                                    }
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share calculation"))
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share calculation result",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Show Steps Button
        if (uiState.output.isNotEmpty() && uiState.explanation != null) {
            OutlinedButton(
                onClick = {
                    focusManager.clearFocus()
                    showExplanation = !showExplanation
                },
                modifier = Modifier.fillMaxWidth()
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

        // Explanation Section
        if (showExplanation && uiState.explanation != null) {
            CalculatorExplanationCard(
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
private fun CalculatorExplanationCard(
    explanation: CalculatorExplanation,
    onDismiss: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            // Summary
            Text(
                text = explanation.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Input 1 Conversion (if not decimal)
            explanation.input1Conversion?.let { part ->
                Spacer(modifier = Modifier.height(spacing.small))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(spacing.small))
                ExplanationPartSection(part)
            }

            // Input 2 Conversion (if not decimal)
            explanation.input2Conversion?.let { part ->
                Spacer(modifier = Modifier.height(spacing.small))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(spacing.small))
                ExplanationPartSection(part)
            }

            // Operation
            Spacer(modifier = Modifier.height(spacing.small))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(spacing.small))

            Text(
                text = explanation.operation.title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(spacing.extraSmall))
            Text(
                text = explanation.operation.description,
                style = MaterialTheme.typography.bodySmall
            )

            // Output Conversion (if not decimal)
            explanation.outputConversion?.let { part ->
                Spacer(modifier = Modifier.height(spacing.small))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(spacing.small))
                ExplanationPartSection(part)
            }

            // Final Result
            Spacer(modifier = Modifier.height(spacing.small))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(spacing.small))

            ElevatedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                ) {
                    Text(
                        text = "Final Result",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = explanation.summary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun ExplanationPartSection(part: ExplanationPart) {
    Text(
        text = part.title,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(spacing.extraSmall))

    part.steps.forEachIndexed { index, step ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Text(
                text = "${index + 1}.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = step.description,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Spacer(modifier = Modifier.height(spacing.small))

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
        ) {
            Text(
                text = "Result",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = part.result,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
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
private fun OperationSelector(
    selectedOperation: Operation,
    onOperationSelected: (Operation) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        Operation.entries.forEachIndexed { index, operation ->
            SegmentedButton(
                selected = selectedOperation == operation,
                onClick = { onOperationSelected(operation) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = Operation.entries.size
                )
            ) {
                Text(
                    text = operation.symbol,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

private fun getKeyboardTypeForBase(base: NumberBase): KeyboardType {
    return when (base) {
        NumberBase.BINARY, NumberBase.OCTAL, NumberBase.DECIMAL -> KeyboardType.Number
        NumberBase.HEXADECIMAL -> KeyboardType.Text
    }
}
