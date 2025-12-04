package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rejowan.numberconverter.domain.model.Difficulty
import com.rejowan.numberconverter.presentation.converter.ConverterScreen
import com.rejowan.numberconverter.presentation.learn.LearnScreen
import com.rejowan.numberconverter.presentation.practice.PracticeScreen

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    onNavigateToLesson: (String) -> Unit,
    showHistory: Boolean = false,
    onHistoryDismissed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Screen.Converter.route
        ) {
            composable(route = Screen.Converter.route) {
                ConverterScreen(
                    showHistory = showHistory,
                    onHistoryDismissed = onHistoryDismissed
                )
            }

            composable(route = Screen.Learn.route) {
                LearnScreen(
                    onLessonClick = onNavigateToLesson
                )
            }

            composable(route = Screen.Practice.route) {
                PracticeScreen(
                    difficulty = Difficulty.MEDIUM,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
