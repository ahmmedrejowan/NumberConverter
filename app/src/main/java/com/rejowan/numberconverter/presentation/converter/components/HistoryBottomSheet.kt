package com.rejowan.numberconverter.presentation.converter.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.domain.model.NumberBase
import com.rejowan.numberconverter.presentation.common.components.EmptyBookmarksState
import com.rejowan.numberconverter.presentation.common.components.EmptyHistoryState
import com.rejowan.numberconverter.presentation.common.components.EmptySearchState
import com.rejowan.numberconverter.presentation.settings.components.ConfirmationSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val TAB_RECENT = 0
private const val TAB_BOOKMARKED = 1

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    historyItems: List<HistoryItem>,
    bookmarkedItems: List<HistoryItem>,
    onDismiss: () -> Unit,
    onItemClick: (HistoryItem) -> Unit,
    onToggleBookmark: (Long) -> Unit,
    onDeleteItem: (HistoryItem) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(TAB_RECENT) }
    var showClearConfirm by remember { mutableStateOf(false) }

    val filteredItems = remember(historyItems, bookmarkedItems, searchQuery, selectedTab) {
        val items = if (selectedTab == TAB_RECENT) historyItems else bookmarkedItems
        if (searchQuery.isEmpty()) items
        else items.filter {
            it.input.contains(searchQuery, ignoreCase = true) ||
                it.output.contains(searchQuery, ignoreCase = true) ||
                it.fromBase.displayName.contains(searchQuery, ignoreCase = true) ||
                it.toBase.displayName.contains(searchQuery, ignoreCase = true)
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (historyItems.isNotEmpty()) {
                    TextButton(onClick = { showClearConfirm = true }) {
                        Icon(
                            imageVector = Icons.Rounded.DeleteSweep,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Clear",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            // Search bar — filled style matching the Converter input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = { Text("Search conversions") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                Icons.Rounded.Clear,
                                contentDescription = "Clear search",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )

            // Tabs — PrimaryTabRow (replaces deprecated TabRow)
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(bottom = 8.dp),
                containerColor = Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == TAB_RECENT,
                    onClick = { selectedTab = TAB_RECENT },
                    text = {
                        TabLabel(
                            label = "Recent",
                            count = historyItems.size,
                            selected = selectedTab == TAB_RECENT
                        )
                    }
                )
                Tab(
                    selected = selectedTab == TAB_BOOKMARKED,
                    onClick = { selectedTab = TAB_BOOKMARKED },
                    text = {
                        TabLabel(
                            label = "Bookmarks",
                            count = bookmarkedItems.size,
                            selected = selectedTab == TAB_BOOKMARKED
                        )
                    }
                )
            }

            if (filteredItems.isEmpty()) {
                when {
                    searchQuery.isNotEmpty() -> EmptySearchState(query = searchQuery)
                    selectedTab == TAB_BOOKMARKED -> EmptyBookmarksState()
                    else -> EmptyHistoryState()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 16.dp)
                ) {
                    items(items = filteredItems, key = { it.id }) { item ->
                        HistoryItemCard(
                            item = item,
                            onItemClick = { onItemClick(item) },
                            onToggleBookmark = { onToggleBookmark(item.id) },
                            onDelete = { onDeleteItem(item) }
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirm) {
        ConfirmationSheet(
            title = "Clear History?",
            message = "This will remove all conversion history. Bookmarked items will be preserved.",
            confirmLabel = "Clear",
            icon = Icons.Rounded.DeleteSweep,
            onConfirm = {
                onClearAll()
                showClearConfirm = false
            },
            onDismiss = { showClearConfirm = false }
        )
    }
}

@Composable
private fun TabLabel(label: String, count: Int, selected: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        if (count > 0) {
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Text(
                    text = count.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoryItemCard(
    item: HistoryItem,
    onItemClick: () -> Unit,
    onToggleBookmark: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val timestamp = remember(item.timestamp) { formatTimestamp(item.timestamp) }

    val containerColor = if (item.isBookmarked) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.06f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }

    Surface(
        onClick = onItemClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // From row
                ConversionLine(
                    base = item.fromBase,
                    value = item.input,
                    accentColor = MaterialTheme.colorScheme.primary,
                    valueWeight = FontWeight.Medium
                )
                // Direction arrow
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                // To row
                ConversionLine(
                    base = item.toBase,
                    value = item.output,
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    valueWeight = FontWeight.SemiBold
                )
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (item.isBookmarked) Icons.Rounded.Star else Icons.Outlined.StarBorder,
                        contentDescription = if (item.isBookmarked) "Remove bookmark" else "Add bookmark",
                        modifier = Modifier.size(20.dp),
                        tint = if (item.isBookmarked) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.85f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ConversionLine(
    base: NumberBase,
    value: String,
    accentColor: Color,
    valueWeight: FontWeight
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = accentColor.copy(alpha = 0.15f)
        ) {
            Text(
                text = base.displayName.uppercase().take(3),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = accentColor,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = valueWeight,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

private val historyDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
private val historyTimeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    return "${historyDateFormat.format(date)} · ${historyTimeFormat.format(date)}"
}
