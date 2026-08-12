package com.rejowan.numberconverter.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.presentation.common.components.AnimatedBottomNav
import com.rejowan.numberconverter.presentation.common.components.NavItem
import com.rejowan.numberconverter.presentation.navigation.BottomNavGraph
import com.rejowan.numberconverter.presentation.navigation.Calculator
import com.rejowan.numberconverter.presentation.navigation.Converter
import com.rejowan.numberconverter.presentation.navigation.Settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val bottomNavController = rememberNavController()
    var selectedNavIndex by rememberSaveable { mutableIntStateOf(0) }
    var showHistorySheet by remember { mutableStateOf(false) }

    // Sync selected index with the actual back-stack entry. Type-safe routes
    // serialize to fully-qualified class names, so we match by suffix.
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        val route = navBackStackEntry?.destination?.route ?: return@LaunchedEffect
        val newIndex = when {
            route.endsWith(".Converter") -> 0
            route.endsWith(".Calculator") -> 1
            route.endsWith(".Settings") -> 2
            else -> -1
        }
        if (newIndex != -1 && newIndex != selectedNavIndex) {
            selectedNavIndex = newIndex
        }
    }

    // Coerced because the saved index survives process death — a value saved by
    // an older build with more tabs would otherwise index out of bounds here.
    val current = NavItem.entries[selectedNavIndex.coerceIn(NavItem.entries.indices)]
    val title = when (current) {
        NavItem.CONVERTER -> stringResource(R.string.title_converter)
        NavItem.CALCULATOR -> stringResource(R.string.title_calculator)
        NavItem.SETTINGS -> stringResource(R.string.title_settings)
    }

    // Settings owns its own large header — hide the TopAppBar there to avoid a
    // redundant "Settings" title above the screen's own headline.
    val showTopBar = current != NavItem.SETTINGS

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (showTopBar) {
                    TopAppBar(
                        title = { Text(text = title) },
                        actions = {
                            if (current == NavItem.CONVERTER) {
                                IconButton(onClick = { showHistorySheet = true }) {
                                    Icon(
                                        imageVector = Icons.Default.History,
                                        contentDescription = "View conversion history",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            titleContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            },
            // Keep status-bar inset; let content bleed behind the bottom nav.
            contentWindowInsets = WindowInsets.statusBars
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                BottomNavGraph(
                    navController = bottomNavController,
                    showHistory = showHistorySheet,
                    onHistoryDismissed = { showHistorySheet = false }
                )
            }
        }

        AnimatedBottomNav(
            selectedIndex = selectedNavIndex,
            onItemClick = { index ->
                if (index == selectedNavIndex) return@AnimatedBottomNav
                // Update the index immediately so the floating circle animates in
                // sync with the tap. The LaunchedEffect above is just a safety net
                // for back-stack changes that originate elsewhere (e.g. system back).
                selectedNavIndex = index
                val route: Any = when (index) {
                    0 -> Converter
                    1 -> Calculator
                    2 -> Settings
                    else -> return@AnimatedBottomNav
                }
                bottomNavController.navigate(route) {
                    popUpTo(Converter) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
