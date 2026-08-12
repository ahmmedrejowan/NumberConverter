package com.rejowan.numberconverter.presentation.converter

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.common.components.BaseSelectorRow
import com.rejowan.numberconverter.presentation.common.components.ErrorBanner
import com.rejowan.numberconverter.presentation.common.components.FieldLabel
import com.rejowan.numberconverter.presentation.common.components.OutputDisplay
import com.rejowan.numberconverter.presentation.common.components.ResultActionBar
import com.rejowan.numberconverter.presentation.common.components.ShowStepsButton
import com.rejowan.numberconverter.presentation.common.util.keyboardTypeFor
import com.rejowan.numberconverter.presentation.converter.components.ExplanationSheet
import com.rejowan.numberconverter.presentation.converter.components.HistoryBottomSheet
import kotlinx.coroutines.launch
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
    val clipboard = LocalClipboard.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showExplanationSheet by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding() // lift content above the keyboard
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 120.dp), // room for the floating bottom nav
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero conversion card — the headline content of the screen.
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        onClick = { focusManager.clearFocus() },
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FieldLabel("From")
                    BaseSelectorRow(
                        selected = uiState.fromBase,
                        onSelect = { viewModel.onFromBaseChanged(it) }
                    )
                    OutlinedTextField(
                        value = uiState.input,
                        onValueChange = { viewModel.onInputChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter ${uiState.fromBase.displayName.lowercase()} value") },
                        isError = uiState.validationError != null,
                        supportingText = uiState.validationError?.let { msg -> { Text(msg) } },
                        // Multi-line wrapping instead of horizontal scroll — capped at 4
                        // lines so a paste of a 10k-char value doesn't blow up the card.
                        minLines = 1,
                        maxLines = 4,
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = keyboardTypeFor(uiState.fromBase),
                            imeAction = ImeAction.Done
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

                    FieldLabel("To")
                    BaseSelectorRow(
                        selected = uiState.toBase,
                        onSelect = { viewModel.onToBaseChanged(it) }
                    )
                    OutputDisplay(output = uiState.output)

                    ResultActionBar(
                        visible = uiState.output.isNotEmpty(),
                        onCopy = {
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(ClipData.newPlainText("Conversion result", uiState.output))
                                )
                                snackbarHostState.showSnackbar("Result copied")
                            }
                        },
                        onShare = {
                            val text = "${uiState.fromBase.displayName}: ${uiState.input}\n" +
                                "${uiState.toBase.displayName}: ${uiState.output}"
                            try {
                                context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        },
                                        "Share conversion"
                                    )
                                )
                            } catch (_: ActivityNotFoundException) {
                                scope.launch { snackbarHostState.showSnackbar("No app available to share with") }
                            }
                        },
                        onClear = { viewModel.clearInput() }
                    )
                }
            }

            ShowStepsButton(
                visible = uiState.output.isNotEmpty() &&
                    uiState.validationError == null &&
                    uiState.explanation != null,
                onClick = {
                    focusManager.clearFocus()
                    showExplanationSheet = true
                }
            )

            // Error banner (non-validation errors)
            uiState.errorMessage?.let { msg ->
                ErrorBanner(message = msg)
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 110.dp)
        )
    }

    // Explanation sheet
    val explanation = uiState.explanation
    if (showExplanationSheet && explanation != null) {
        ExplanationSheet(
            explanation = explanation,
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
            onClearAll = { viewModel.clearHistoryKeepingBookmarks() }
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
