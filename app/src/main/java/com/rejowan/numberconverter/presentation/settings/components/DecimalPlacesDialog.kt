package com.rejowan.numberconverter.presentation.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.rejowan.numberconverter.presentation.common.theme.spacing
import kotlin.math.roundToInt

@Composable
fun DecimalPlacesDialog(
    currentPlaces: Int,
    onPlacesSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = spacing
    var sliderValue by remember { mutableFloatStateOf(currentPlaces.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Decimal Places") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${sliderValue.roundToInt()} decimal places",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(bottom = spacing.medium)
                )

                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 5f..30f,
                    steps = 24
                )

                Text(
                    text = "Preview: 3.${("1").repeat(sliderValue.roundToInt())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = spacing.small)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onPlacesSelected(sliderValue.roundToInt())
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
