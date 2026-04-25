package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rejowan.numberconverter.presentation.home.HomeScreen
import com.rejowan.numberconverter.presentation.onboarding.OnboardingScreen
import com.rejowan.numberconverter.presentation.onboarding.OnboardingViewModel
import org.koin.androidx.compose.koinViewModel

private const val TRANSITION_DURATION = 300

@Composable
fun ParentNavGraph(
    startDestination: Any = Home
) {
    val parentNavController = rememberNavController()

    NavHost(
        navController = parentNavController,
        startDestination = startDestination,
        enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
        exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) }
    ) {
        composable<Onboarding> {
            val viewModel: OnboardingViewModel = koinViewModel()
            OnboardingScreen(
                onComplete = {
                    viewModel.completeOnboarding()
                    parentNavController.navigate(Home) {
                        popUpTo<Onboarding> { inclusive = true }
                    }
                }
            )
        }

        composable<Home> {
            HomeScreen()
        }
    }
}
