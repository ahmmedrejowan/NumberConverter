package com.rejowan.numberconverter.presentation.converter.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.NumberBase

/**
 * Custom segmented selector for number bases. Each pill is full-width-distributed;
 * the selected pill gets a solid `primary` fill, deselected pills sit on
 * `surfaceContainerHigh` for a quiet contrast. Used for both the FROM and TO
 * rows in the Converter hero.
 */
@Composable
fun BaseSelectorRow(
    selected: NumberBase,
    onSelect: (NumberBase) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NumberBase.BINARY to "BIN",
        NumberBase.OCTAL to "OCT",
        NumberBase.DECIMAL to "DEC",
        NumberBase.HEXADECIMAL to "HEX"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach { (base, label) ->
            BasePill(
                label = label,
                selected = base == selected,
                onClick = { onSelect(base) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun BasePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.primary
        else MaterialTheme.colorScheme.surfaceContainerHigh,
        label = "container"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "content"
    )

    Surface(
        modifier = modifier
            .height(32.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = containerColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
