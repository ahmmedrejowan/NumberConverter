package com.rejowan.numberconverter.presentation.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.rejowan.numberconverter.R
import com.rejowan.numberconverter.presentation.home.components.BottomNavigationBar
import com.rejowan.numberconverter.presentation.navigation.HomeNavGraph
import com.rejowan.numberconverter.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToLesson: (String) -> Unit
) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    var showHistorySheet by remember { mutableStateOf(false) }

    val title = when {
        currentDestination?.hierarchy?.any { it.route == Screen.Converter.route } == true ->
            stringResource(R.string.title_converter)
        currentDestination?.hierarchy?.any { it.route == Screen.Calculator.route } == true ->
            stringResource(R.string.title_calculator)
        currentDestination?.hierarchy?.any { it.route == Screen.Learn.route } == true ->
            stringResource(R.string.title_learn)
        currentDestination?.hierarchy?.any { it.route == Screen.Practice.route } == true ->
            stringResource(R.string.title_practice)
        else -> stringResource(R.string.app_name)
    }

    val isConverterScreen = currentDestination?.hierarchy?.any { it.route == Screen.Converter.route } == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = title) },
                actions = {
                    if (isConverterScreen) {
                        IconButton(onClick = { showHistorySheet = true }) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History"
                            )
                        }
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.settings)
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                navController = homeNavController,
                currentDestination = currentDestination
            )
        }
    ) { paddingValues ->
        HomeNavGraph(
            navController = homeNavController,
            onNavigateToLesson = onNavigateToLesson,
            showHistory = showHistorySheet,
            onHistoryDismissed = { showHistorySheet = false },
            modifier = Modifier.padding(paddingValues)
        )
    }
}
