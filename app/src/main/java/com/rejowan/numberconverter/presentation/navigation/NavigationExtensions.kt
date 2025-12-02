package com.rejowan.numberconverter.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

/**
 * Navigate to a bottom navigation destination with proper back stack handling.
 * Pops up to the start destination and avoids multiple copies of the same destination.
 */
fun NavHostController.navigateToBottomNavDestination(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Navigate with a single top instance behavior.
 * Prevents multiple instances of the same destination.
 */
fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

/**
 * Navigate and clear the entire back stack.
 * Useful for navigating to a new root destination.
 */
fun NavHostController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

/**
 * Safely navigate back, returning false if back stack is empty.
 */
fun NavController.safePopBackStack(): Boolean {
    return if (previousBackStackEntry != null) {
        popBackStack()
        true
    } else {
        false
    }
}
