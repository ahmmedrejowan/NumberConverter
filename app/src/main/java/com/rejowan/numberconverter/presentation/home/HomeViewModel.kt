package com.rejowan.numberconverter.presentation.home

import androidx.lifecycle.ViewModel
import com.rejowan.numberconverter.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * ViewModel for the Home screen that manages the bottom navigation state.
 */
class HomeViewModel : ViewModel() {

    private val _currentTab = MutableStateFlow(Screen.Converter.route)
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    /**
     * Updates the current tab when the user navigates to a different bottom nav destination.
     */
    fun onTabSelected(route: String) {
        _currentTab.value = route
    }

    /**
     * Returns the title resource key for the given route.
     */
    fun getTitleForRoute(route: String): String {
        return when (route) {
            Screen.Converter.route -> "Convert"
            Screen.Learn.route -> "Learn"
            Screen.Practice.route -> "Practice"
            else -> "Number Converter"
        }
    }
}
