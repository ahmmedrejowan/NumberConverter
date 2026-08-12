package com.rejowan.numberconverter.presentation.calculator

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.clickable
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
import com.rejowan.numberconverter.presentation.calculator.components.CalculatorExplanationSheet
import com.rejowan.numberconverter.presentation.common.components.BaseSelectorRow
import com.rejowan.numberconverter.presentation.common.components.ErrorBanner
import com.rejowan.numberconverter.presentation.common.components.FieldLabel
import com.rejowan.numberconverter.presentation.common.components.OperationSelectorRow
import com.rejowan.numberconverter.presentation.common.components.OutputDisplay
import com.rejowan.numberconverter.presentation.common.components.ResultActionBar
import com.rejowan.numberconverter.presentation.common.components.ShowStepsButton
import com.rejowan.numberconverter.presentation.common.util.keyboardTypeFor
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

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
                .imePadding()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 120.dp), // room for the floating bottom nav
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Hero calculation card — mirrors the Converter's hero so the two
            // tabs read as one system.
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
                    // ---------- First operand ----------
                    FieldLabel("First number")
                    BaseSelectorRow(
                        selected = uiState.input1Base,
                        onSelect = { viewModel.onInput1BaseChanged(it) }
                    )
                    OperandField(
                        value = uiState.input1,
                        base = uiState.input1Base,
                        error = uiState.validation1Error,
                        onValueChange = { viewModel.onInput1Changed(it) }
                    )

                    // ---------- Operator + swap ----------
                    OperationSelectorRow(
                        selected = uiState.operation,
                        onSelect = { viewModel.onOperationChanged(it) }
                    )
                    SwapButton(
                        onSwap = {
                            focusManager.clearFocus()
                            viewModel.swapInputs()
                        }
                    )

                    // ---------- Second operand ----------
                    FieldLabel("Second number")
                    BaseSelectorRow(
                        selected = uiState.input2Base,
                        onSelect = { viewModel.onInput2BaseChanged(it) }
                    )
                    OperandField(
                        value = uiState.input2,
                        base = uiState.input2Base,
                        error = uiState.validation2Error,
                        onValueChange = { viewModel.onInput2Changed(it) }
                    )

                    // ---------- Result ----------
                    FieldLabel("Result")
                    BaseSelectorRow(
                        selected = uiState.outputBase,
                        onSelect = { viewModel.onOutputBaseChanged(it) }
                    )
                    OutputDisplay(output = uiState.output)

                    ResultActionBar(
                        visible = uiState.output.isNotEmpty(),
                        onCopy = {
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(ClipData.newPlainText("Calculation result", uiState.output))
                                )
                                snackbarHostState.showSnackbar("Result copied")
                            }
                        },
                        onShare = {
                            val text = buildString {
                                append("${uiState.input1Base.displayName}: ${uiState.input1}")
                                append(" ${uiState.operation.symbol} ")
                                append("${uiState.input2Base.displayName}: ${uiState.input2}")
                                append(" = ")
                                append("${uiState.outputBase.displayName}: ${uiState.output}")
                            }
                            try {
                                context.startActivity(
                                    Intent.createChooser(
                                        Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, text)
                                        },
                                        "Share calculation"
                                    )
                                )
                            } catch (_: ActivityNotFoundException) {
                                scope.launch { snackbarHostState.showSnackbar("No app available to share with") }
                            }
                        },
                        onClear = { viewModel.clearAll() }
                    )
                }
            }

            ShowStepsButton(
                visible = uiState.output.isNotEmpty() && uiState.explanation != null,
                onClick = {
                    focusManager.clearFocus()
                    showExplanationSheet = true
                }
            )

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

    val explanation = uiState.explanation
    if (showExplanationSheet && explanation != null) {
        CalculatorExplanationSheet(
            explanation = explanation,
            input1 = uiState.input1,
            input1Base = uiState.input1Base,
            input2 = uiState.input2,
            input2Base = uiState.input2Base,
            operation = uiState.operation,
            output = uiState.output,
            outputBase = uiState.outputBase,
            onDismiss = { showExplanationSheet = false }
        )
    }
}

// ============================================================================
// Sub-components
// ============================================================================

@Composable
private fun OperandField(
    value: String,
    base: NumberBase,
    error: String?,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Enter ${base.displayName.lowercase()} value") },
        isError = error != null,
        supportingText = error?.let { msg -> { Text(msg) } },
        // Wrap rather than scroll horizontally, capped so a long paste can't
        // push the rest of the card off screen.
        minLines = 1,
        maxLines = 3,
        textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardTypeFor(base),
            imeAction = ImeAction.Done
        ),
        shape = RoundedCornerShape(14.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    )
}

@Composable
private fun SwapButton(onSwap: () -> Unit) {
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
                contentDescription = "Swap the two numbers",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
