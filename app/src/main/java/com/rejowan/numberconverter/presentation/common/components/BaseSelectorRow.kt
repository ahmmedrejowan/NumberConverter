package com.rejowan.numberconverter.presentation.common.components

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.domain.model.Operation

/**
 * Segmented selector for number bases. Each pill is full-width-distributed; the
 * selected pill gets a solid `primary` fill, deselected pills sit on
 * `surfaceContainerHigh` for a quiet contrast.
 *
 * Shared by the Converter hero (FROM / TO) and the Calculator hero
 * (operand 1 / operand 2 / result) so both screens read as one system.
 */
@Composable
fun BaseSelectorRow(
    selected: NumberBase,
    onSelect: (NumberBase) -> Unit,
    modifier: Modifier = Modifier
) {
    SelectorRow(
        modifier = modifier,
        options = NumberBase.entries,
        selected = selected,
        label = { it.displayName.uppercase().take(3) },
        contentDescription = { it.displayName },
        onSelect = onSelect
    )
}

/**
 * Same pill treatment as [BaseSelectorRow], for the arithmetic operator. Keeping
 * the two visually identical is deliberate — the Calculator hero reads as three
 * stacked "pick one" rows rather than a mix of chips and segmented buttons.
 */
@Composable
fun OperationSelectorRow(
    selected: Operation,
    onSelect: (Operation) -> Unit,
    modifier: Modifier = Modifier
) {
    SelectorRow(
        modifier = modifier,
        options = Operation.entries,
        selected = selected,
        label = { it.symbol },
        contentDescription = { it.displayName },
        onSelect = onSelect
    )
}

@Composable
private fun <T> SelectorRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    contentDescription: (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        options.forEach { option ->
            SelectorPill(
                label = label(option),
                contentDescription = contentDescription(option),
                selected = option == selected,
                onClick = { onSelect(option) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SelectorPill(
    label: String,
    contentDescription: String,
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
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable(
                onClick = onClick,
                role = Role.RadioButton,
                onClickLabel = contentDescription
            ),
        shape = RoundedCornerShape(10.dp),
        color = containerColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}
