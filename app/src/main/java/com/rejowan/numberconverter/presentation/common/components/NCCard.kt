package com.rejowan.numberconverter.presentation.common.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NCCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            content = content
        )
    }
}

@Composable
fun NCElevatedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        ElevatedCard(
            onClick = onClick,
            modifier = modifier,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            content = content
        )
    } else {
        ElevatedCard(
            modifier = modifier,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
            content = content
        )
    }
}

@Composable
fun NCOutlinedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    if (onClick != null) {
        OutlinedCard(
            onClick = onClick,
            modifier = modifier,
            content = content
        )
    } else {
        OutlinedCard(
            modifier = modifier,
            content = content
        )
    }
}
