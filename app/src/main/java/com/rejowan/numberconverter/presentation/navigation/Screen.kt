package com.rejowan.numberconverter.presentation.navigation

sealed class Screen(val route: String) {
    // Parent Navigation Screens
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data class LessonDetail(val lessonId: String = "{lessonId}") : Screen("lesson/{lessonId}") {
        fun createRoute(lessonId: String) = "lesson/$lessonId"
    }

    // Home Navigation Screens (Bottom Nav)
    data object Converter : Screen("converter")
    data object Learn : Screen("learn")
    data object Practice : Screen("practice")
}
