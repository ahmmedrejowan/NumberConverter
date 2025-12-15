package com.rejowan.numberconverter.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rejowan.numberconverter.presentation.home.HomeScreen
import com.rejowan.numberconverter.presentation.lesson.LessonDetailScreen
import com.rejowan.numberconverter.presentation.practice.PracticeSessionScreen
import com.rejowan.numberconverter.presentation.settings.SettingsScreen

private const val TRANSITION_DURATION = 300

@Composable
fun ParentNavGraph() {
    val parentNavController = rememberNavController()

    NavHost(
        navController = parentNavController,
        startDestination = Screen.Home.route,
        enterTransition = {
            fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.End,
                    animationSpec = tween(TRANSITION_DURATION)
                )
        }
    ) {
        composable(
            route = Screen.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(animationSpec = tween(TRANSITION_DURATION)) }
        ) {
            HomeScreen(
                onNavigateToSettings = {
                    parentNavController.navigate(Screen.Settings.route)
                },
                onNavigateToLesson = { lessonId ->
                    parentNavController.navigate(Screen.LessonDetail(lessonId).createRoute(lessonId))
                },
                onNavigateToPracticeSession = { practiceType ->
                    parentNavController.navigate(Screen.PracticeSession().createRoute(practiceType))
                }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    parentNavController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.LessonDetail().route,
            arguments = listOf(
                navArgument("lessonId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(
                lessonId = lessonId,
                onNavigateBack = {
                    parentNavController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.PracticeSession().route,
            arguments = listOf(
                navArgument("practiceType") { type = NavType.StringType }
            ),
            enterTransition = {
                fadeIn(animationSpec = tween(TRANSITION_DURATION)) +
                    scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(TRANSITION_DURATION)
                    )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(TRANSITION_DURATION)) +
                    scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(TRANSITION_DURATION)
                    )
            }
        ) { backStackEntry ->
            val practiceType = backStackEntry.arguments?.getString("practiceType") ?: "conversion"
            PracticeSessionScreen(
                practiceType = practiceType,
                onNavigateBack = {
                    parentNavController.popBackStack()
                }
            )
        }
    }
}
