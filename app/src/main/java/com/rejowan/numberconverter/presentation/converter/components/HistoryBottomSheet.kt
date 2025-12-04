package com.rejowan.numberconverter.presentation.converter.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rejowan.numberconverter.domain.model.HistoryItem
import com.rejowan.numberconverter.presentation.common.theme.spacing
import java.text.SimpleDateFormat
import java.util.*

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
    var selectedTab by remember { mutableStateOf(0) }
    var showClearDialog by remember { mutableStateOf(false) }

    // Filter items based on search query
    val filteredItems = remember(historyItems, searchQuery, selectedTab) {
        val items = if (selectedTab == 0) historyItems else bookmarkedItems
        if (searchQuery.isEmpty()) {
            items
        } else {
            items.filter {
                it.input.contains(searchQuery, ignoreCase = true) ||
                        it.output.contains(searchQuery, ignoreCase = true) ||
                        it.fromBase.displayName.contains(searchQuery, ignoreCase = true) ||
                        it.toBase.displayName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = spacing.medium)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.medium),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Conversion History",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(spacing.extraSmall)) {
                    if (historyItems.isNotEmpty()) {
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear all"
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                }
            }

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.small),
                placeholder = { Text("Search conversions...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.padding(bottom = spacing.small)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Recent (${historyItems.size})") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Bookmarks (${bookmarkedItems.size})") },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) }
                )
            }

            // Content
            if (filteredItems.isEmpty()) {
                EmptyHistoryView(
                    isBookmarked = selectedTab == 1,
                    hasSearchQuery = searchQuery.isNotEmpty()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(spacing.small),
                    contentPadding = PaddingValues(bottom = spacing.large)
                ) {
                    items(
                        items = filteredItems,
                        key = { it.id }
                    ) { item ->
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

    // Clear All Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear History?") },
            text = { Text("This will delete all conversion history. Bookmarked items will be preserved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearAll()
                        showClearDialog = false
                    }
                ) {
                    Text("Clear All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyHistoryView(
    isBookmarked: Boolean,
    hasSearchQuery: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            Icon(
                imageVector = when {
                    hasSearchQuery -> Icons.Default.SearchOff
                    isBookmarked -> Icons.Default.StarBorder
                    else -> Icons.Default.History
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline
            )
            Text(
                text = when {
                    hasSearchQuery -> "No results found"
                    isBookmarked -> "No bookmarks yet"
                    else -> "No history yet"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = when {
                    hasSearchQuery -> "Try a different search term"
                    isBookmarked -> "Tap the star icon to bookmark conversions"
                    else -> "Start converting numbers to see them here"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline
            )
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
    val dateFormatter = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(item.timestamp) {
        dateFormatter.format(Date(item.timestamp))
    }

    ElevatedCard(
        onClick = onItemClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(spacing.medium)
        ) {
            // Conversion info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // From
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Text(
                            text = item.fromBase.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = item.input,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Arrow
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(vertical = 2.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )

                    // To
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.small)
                    ) {
                        Text(
                            text = item.toBase.displayName,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = item.output,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bookmark button
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (item.isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = if (item.isBookmarked) "Remove bookmark" else "Add bookmark",
                        tint = if (item.isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.small))

            // Footer with timestamp and actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
