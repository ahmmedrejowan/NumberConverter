package com.rejowan.numberconverter.presentation.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.rejowan.numberconverter.presentation.common.theme.spacing

@Composable
fun FontSizeDialog(
    currentSize: String,
    onSizeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = spacing
    val sizes = listOf(
        "small" to "Small",
        "medium" to "Medium",
        "large" to "Large"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Font Size") },
        text = {
            Column {
                sizes.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSizeSelected(value) }
                            .padding(vertical = spacing.small),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = currentSize == value,
                            onClick = { onSizeSelected(value) }
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = spacing.small)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
