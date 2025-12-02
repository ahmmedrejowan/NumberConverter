package com.rejowan.numberconverter.presentation.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.rejowan.numberconverter.presentation.common.theme.spacing

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    val spacing = spacing
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(
            horizontal = spacing.medium,
            vertical = spacing.small
        )
    )
}
