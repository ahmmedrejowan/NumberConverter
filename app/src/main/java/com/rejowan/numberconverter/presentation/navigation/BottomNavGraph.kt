package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rejowan.numberconverter.presentation.calculator.CalculatorScreen
import com.rejowan.numberconverter.presentation.converter.ConverterScreen
import com.rejowan.numberconverter.presentation.settings.SettingsScreen

private const val TAB_TRANSITION_DURATION = 200

/**
 * Nested NavHost for the bottom-nav tabs. Hosted inside [HomeScreen] with its
 * own NavController so tab state is preserved independently of any parent
 * navigation.
 */
@Composable
fun BottomNavGraph(
    navController: NavHostController,
    showHistory: Boolean,
    onHistoryDismissed: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Converter,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(TAB_TRANSITION_DURATION)) },
        exitTransition = { fadeOut(animationSpec = tween(TAB_TRANSITION_DURATION)) },
        popEnterTransition = { fadeIn(animationSpec = tween(TAB_TRANSITION_DURATION)) },
        popExitTransition = { fadeOut(animationSpec = tween(TAB_TRANSITION_DURATION)) }
    ) {
        composable<Converter> {
            ConverterScreen(
                showHistory = showHistory,
                onHistoryDismissed = onHistoryDismissed
            )
        }
        composable<Calculator> {
            CalculatorScreen()
        }
        composable<Settings> {
            SettingsScreen()
        }
    }
}
