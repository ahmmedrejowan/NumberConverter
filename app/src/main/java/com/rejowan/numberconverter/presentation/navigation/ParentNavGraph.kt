package com.rejowan.numberconverter.presentation.navigation

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

@Composable
fun ParentNavGraph() {
    val parentNavController = rememberNavController()

    NavHost(
        navController = parentNavController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) {
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
            )
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
