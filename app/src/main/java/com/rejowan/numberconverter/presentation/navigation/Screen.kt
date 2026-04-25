package com.rejowan.numberconverter.presentation.navigation

sealed class Screen(val route: String) {
    // Parent Navigation Screens
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")

    // Home Navigation Screens (Bottom Nav)
    data object Converter : Screen("converter")
    data object Calculator : Screen("calculator")
    data object Learn : Screen("learn")
    data object Settings : Screen("settings")
}
