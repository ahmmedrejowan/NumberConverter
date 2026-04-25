package com.rejowan.numberconverter.presentation.converter

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.converter.components.BaseSelectorRow
import com.rejowan.numberconverter.presentation.converter.components.ExplanationSheet
import com.rejowan.numberconverter.presentation.converter.components.HistoryBottomSheet
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

    val focusManager = LocalFocusManager.current
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    var showExplanationSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding() // lift content above the keyboard
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 120.dp), // bottom padding for floating bottom nav
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero conversion card — the headline content of the screen.
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { focusManager.clearFocus() },
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null
                ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                BaseSelectorRow(
                    selected = uiState.fromBase,
                    onSelect = { viewModel.onFromBaseChanged(it) }
                )
                OutlinedTextField(
                    value = uiState.input,
                    onValueChange = { raw ->
                        viewModel.onInputChanged(filterInputForBase(raw, uiState.fromBase))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter ${uiState.fromBase.displayName.lowercase()} value") },
                    isError = uiState.validationError != null,
                    supportingText = uiState.validationError?.let { msg ->
                        { Text(msg) }
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = getKeyboardTypeForBase(uiState.fromBase)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )

                // Compact swap button between the FROM and TO halves
                SwapButton(
                    onSwap = {
                        focusManager.clearFocus()
                        viewModel.swapBases()
                    },
                    fromBase = uiState.fromBase,
                    toBase = uiState.toBase
                )

                BaseSelectorRow(
                    selected = uiState.toBase,
                    onSelect = { viewModel.onToBaseChanged(it) }
                )
                OutputDisplay(output = uiState.output)

                // Action toolbar — visible only when there's a result
                AnimatedVisibility(
                    visible = uiState.output.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ActionPill(
                            icon = Icons.Outlined.ContentCopy,
                            label = "Copy",
                            onClick = {
                                clipboardManager.setText(AnnotatedString(uiState.output))
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ActionPill(
                            icon = Icons.Outlined.Share,
                            label = "Share",
                            onClick = {
                                val text = "${uiState.fromBase.displayName}: ${uiState.input}\n" +
                                        "${uiState.toBase.displayName}: ${uiState.output}"
                                context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        },
                                        "Share conversion"
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                        ActionPill(
                            icon = Icons.Outlined.DeleteOutline,
                            label = "Clear",
                            onClick = { viewModel.clearInput() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Show Steps button — opens the explanation as a bottom sheet
        AnimatedVisibility(
            visible = uiState.output.isNotEmpty() && uiState.validationError == null && uiState.explanation != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            FilledTonalButton(
                onClick = {
                    focusManager.clearFocus()
                    showExplanationSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Show steps",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Error banner (non-validation errors)
        uiState.errorMessage?.let { msg ->
            ErrorBanner(message = msg)
        }
    }

    // Explanation sheet
    if (showExplanationSheet && uiState.explanation != null) {
        ExplanationSheet(
            explanation = uiState.explanation!!,
            fromBase = uiState.fromBase,
            toBase = uiState.toBase,
            input = uiState.input,
            output = uiState.output,
            onDismiss = { showExplanationSheet = false }
        )
    }

    // History sheet (driven from HomeScreen TopAppBar)
    if (showHistory) {
        HistoryBottomSheet(
            historyItems = historyItems,
            bookmarkedItems = bookmarkedItems,
            onDismiss = onHistoryDismissed,
            onItemClick = { item ->
                viewModel.restoreFromHistory(item)
                onHistoryDismissed()
            },
            onToggleBookmark = { id -> viewModel.toggleBookmark(id) },
            onDeleteItem = { item -> viewModel.deleteHistoryItem(item) },
            onClearAll = { viewModel.clearAllHistory() }
        )
    }
}

// ============================================================================
// Sub-components
// ============================================================================

@Composable
private fun SwapButton(
    onSwap: () -> Unit,
    fromBase: NumberBase,
    toBase: NumberBase
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onSwap,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Swap ${fromBase.displayName} and ${toBase.displayName}",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun OutputDisplay(output: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 64.dp)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (output.isEmpty()) {
                Text(
                    text = "Result will appear here",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            } else {
                Text(
                    text = output,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ActionPill(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorBanner(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.ErrorOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// ============================================================================
// Helpers
// ============================================================================

private fun filterInputForBase(input: String, base: NumberBase): String {
    val pattern = when (base) {
        NumberBase.BINARY -> "[01.]"
        NumberBase.OCTAL -> "[0-7.]"
        NumberBase.DECIMAL -> "[0-9.]"
        NumberBase.HEXADECIMAL -> "[0-9a-fA-F.]"
    }
    return input.filter { it.toString().matches(Regex(pattern)) }
}

private fun getKeyboardTypeForBase(base: NumberBase): KeyboardType = when (base) {
    NumberBase.BINARY, NumberBase.OCTAL, NumberBase.DECIMAL -> KeyboardType.Number
    NumberBase.HEXADECIMAL -> KeyboardType.Text
}
