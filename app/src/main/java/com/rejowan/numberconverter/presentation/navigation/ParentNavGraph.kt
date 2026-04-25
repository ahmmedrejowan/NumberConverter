package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.presentation.home.HomeScreen
import com.rejowan.numberconverter.presentation.onboarding.OnboardingScreen
import com.rejowan.numberconverter.presentation.onboarding.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

private const val TRANSITION_DURATION = 300

@Composable
fun ParentNavGraph(
    preferencesManager: PreferencesManager = koinInject()
) {
    val parentNavController = rememberNavController()
    val onboardingCompleted by preferencesManager.onboardingCompleted.collectAsState(initial = null)

    // Wait for the flag to load before deciding the start destination, so we
    // don't briefly show onboarding to a returning user.
    if (onboardingCompleted == null) return

    val startDestination = if (onboardingCompleted == true) Screen.Home.route else Screen.Onboarding.route

    NavHost(
        navController = parentNavController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) }
    ) {
        composable(route = Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = koinViewModel()
            OnboardingScreen(
                onComplete = {
                    viewModel.completeOnboarding()
                    parentNavController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Home.route) {
            HomeScreen()
        }
    }
}
