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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.TextFieldDefaults
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
import android.content.Intent
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.common.theme.spacing
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = koinViewModel(),
    showHistory: Boolean = false,
    onHistoryDismissed: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val historyItems by viewModel.historyItems.collectAsState()
    val bookmarkedItems by viewModel.bookmarkedItems.collectAsState()
    val spacing = spacing
    var showExplanation by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        // Main Conversion Card
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
                // Compact base chips
                CompactBaseSelector(
                    selectedBase = uiState.fromBase,
                    onBaseSelected = { viewModel.onFromBaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Input field
                OutlinedTextField(
                    value = uiState.input,
                    onValueChange = {
                        val filtered = filterInputForBase(it, uiState.fromBase)
                        viewModel.onInputChanged(filtered)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.fromBase.displayName) },
                    placeholder = { Text("Enter value") },
                    supportingText = {
                        if (uiState.validationError != null) {
                            Text(uiState.validationError!!)
                        }
                    },
                    isError = uiState.validationError != null,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = getKeyboardTypeForBase(uiState.fromBase)
                    )
                )

                // Swap button
                IconButton(
                    onClick = { viewModel.swapBases() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(48.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .semantics {
                            contentDescription = "Swap input and output bases. Currently converting from ${uiState.fromBase.displayName} to ${uiState.toBase.displayName}"
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapVert,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Compact base chips
                CompactBaseSelector(
                    selectedBase = uiState.toBase,
                    onBaseSelected = { viewModel.onToBaseChanged(it) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Output display
                OutlinedTextField(
                    value = uiState.output,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(uiState.toBase.displayName) },
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
                                onClick = { viewModel.clearInput() },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Clear input",
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    val shareText = "${uiState.fromBase.displayName}: ${uiState.input}\n${uiState.toBase.displayName}: ${uiState.output}"
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share conversion"))
                                },
                                modifier = Modifier.size(48.dp)
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Share conversion result",
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Action buttons
        if (uiState.output.isNotEmpty() && uiState.validationError == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                OutlinedButton(
                    onClick = {
                        focusManager.clearFocus()
                        showExplanation = !showExplanation
                    },
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
                fromBase = uiState.fromBase,
                toBase = uiState.toBase,
                input = uiState.input,
                output = uiState.output,
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

    // History Bottom Sheet
    if (showHistory) {
        com.rejowan.numberconverter.presentation.converter.components.HistoryBottomSheet(
            historyItems = historyItems,
            bookmarkedItems = bookmarkedItems,
            onDismiss = onHistoryDismissed,
            onItemClick = { item ->
                viewModel.restoreFromHistory(item)
                onHistoryDismissed()
            },
            onToggleBookmark = { id ->
                viewModel.toggleBookmark(id)
            },
            onDeleteItem = { item ->
                viewModel.deleteHistoryItem(item)
            },
            onClearAll = {
                viewModel.clearAllHistory()
            }
        )
    }
}

// Helper function to filter input based on number base
private fun filterInputForBase(input: String, base: NumberBase): String {
    val allowedChars = when (base) {
        NumberBase.BINARY -> "[01.]"
        NumberBase.OCTAL -> "[0-7.]"
        NumberBase.DECIMAL -> "[0-9.]"
        NumberBase.HEXADECIMAL -> "[0-9a-fA-F.]"
    }
    return input.filter { it.toString().matches(Regex(allowedChars)) }
}

// Helper function to get keyboard type based on number base
private fun getKeyboardTypeForBase(base: NumberBase): KeyboardType {
    return when (base) {
        NumberBase.BINARY, NumberBase.OCTAL, NumberBase.DECIMAL -> KeyboardType.Number
        NumberBase.HEXADECIMAL -> KeyboardType.Text
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

// Helper function to get short base name
private fun getShortBaseName(base: NumberBase): String {
    return when (base) {
        NumberBase.BINARY -> "BIN"
        NumberBase.OCTAL -> "OCT"
        NumberBase.DECIMAL -> "DEC"
        NumberBase.HEXADECIMAL -> "HEX"
    }
}

@Composable
private fun ExplanationCard(
    explanation: com.rejowan.numberconverter.domain.model.Explanation,
    fromBase: NumberBase,
    toBase: NumberBase,
    input: String,
    output: String,
    onDismiss: () -> Unit
) {

    OutlinedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            // Summary section
            Text(
                text = explanation.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Integral Part
            explanation.integralPart?.let { integral ->
                Spacer(modifier = Modifier.height(spacing.small))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    text = integral.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(spacing.extraSmall))

                integral.steps.forEachIndexed { index, step ->
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

                // Part Result Card
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Text(
                            text = "Integral Part Result",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${integral.result}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Fractional Part
            explanation.fractionalPart?.let { fractional ->
                Spacer(modifier = Modifier.height(spacing.small))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(spacing.small))

                Text(
                    text = fractional.title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(spacing.extraSmall))

                fractional.steps.forEachIndexed { index, step ->
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

                // Part Result Card
                OutlinedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(spacing.medium),
                        verticalArrangement = Arrangement.spacedBy(spacing.extraSmall)
                    ) {
                        Text(
                            text = "Fractional Part Result",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "${fractional.result}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Final Result Section
            Spacer(modifier = Modifier.height(spacing.small))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(spacing.small))

            // Final Result Card
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
                        text = "${fromBase.displayName}: $input",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${toBase.displayName}: $output",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
