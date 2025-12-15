package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rejowan.numberconverter.presentation.calculator.CalculatorScreen
import com.rejowan.numberconverter.presentation.converter.ConverterScreen
import com.rejowan.numberconverter.presentation.learn.LearnScreen
import com.rejowan.numberconverter.presentation.practice.PracticeScreen

private const val TAB_TRANSITION_DURATION = 200

@Composable
fun HomeNavGraph(
    navController: NavHostController,
    onNavigateToLesson: (String) -> Unit,
    onNavigateToPracticeSession: (String) -> Unit,
    showHistory: Boolean = false,
    onHistoryDismissed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        NavHost(
            navController = navController,
            startDestination = Screen.Converter.route,
            enterTransition = { fadeIn(animationSpec = tween(TAB_TRANSITION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(TAB_TRANSITION_DURATION)) },
            popEnterTransition = { fadeIn(animationSpec = tween(TAB_TRANSITION_DURATION)) },
            popExitTransition = { fadeOut(animationSpec = tween(TAB_TRANSITION_DURATION)) }
        ) {
            composable(route = Screen.Converter.route) {
                ConverterScreen(
                    showHistory = showHistory,
                    onHistoryDismissed = onHistoryDismissed
                )
            }

            composable(route = Screen.Calculator.route) {
                CalculatorScreen()
            }

            composable(route = Screen.Learn.route) {
                LearnScreen(
                    onLessonClick = onNavigateToLesson
                )
            }

            composable(route = Screen.Practice.route) {
                PracticeScreen(
                    onNavigateToPracticeSession = onNavigateToPracticeSession
                )
            }
        }
    }
}
