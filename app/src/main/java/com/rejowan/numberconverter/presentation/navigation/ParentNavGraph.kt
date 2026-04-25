package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejowan.numberconverter.presentation.home.HomeScreen

private const val TRANSITION_DURATION = 300

@Composable
fun ParentNavGraph() {
    val parentNavController = rememberNavController()

    NavHost(
        navController = parentNavController,
        startDestination = Screen.Home.route,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) }
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen()
        }
    }
}
