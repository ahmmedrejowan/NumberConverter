package com.rejowan.numberconverter.presentation.converter

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.common.theme.spacing
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConverterScreen(
    viewModel: ConverterViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val spacing = spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.medium)
    ) {
        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                // Base selector
                BaseDropdown(
                    label = "From",
                    selectedBase = uiState.fromBase,
                    onBaseSelected = { viewModel.onFromBaseChanged(it) }
                )

                // Input field
                OutlinedTextField(
                    value = uiState.input,
                    onValueChange = { viewModel.onInputChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Input") },
                    supportingText = {
                        if (uiState.validationError != null) {
                            Text(uiState.validationError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    isError = uiState.validationError != null,
                    singleLine = true
                )
            }
        }

        // Swap button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            FilledIconButton(
                onClick = { viewModel.swapBases() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap bases"
                )
            }
        }

        // Output Card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.small)
            ) {
                // Base selector
                BaseDropdown(
                    label = "To",
                    selectedBase = uiState.toBase,
                    onBaseSelected = { viewModel.onToBaseChanged(it) }
                )

                // Output field (read-only)
                OutlinedTextField(
                    value = uiState.output,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Output") },
                    readOnly = true,
                    singleLine = true
                )
            }
        }

        // Error message
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseDropdown(
    label: String,
    selectedBase: NumberBase,
    onBaseSelected: (NumberBase) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val bases = listOf(
        NumberBase.BINARY,
        NumberBase.OCTAL,
        NumberBase.DECIMAL,
        NumberBase.HEXADECIMAL
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selectedBase.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            bases.forEach { base ->
                DropdownMenuItem(
                    text = { Text(base.displayName) },
                    onClick = {
                        onBaseSelected(base)
                        expanded = false
                    }
                )
            }
        }
    }
}
