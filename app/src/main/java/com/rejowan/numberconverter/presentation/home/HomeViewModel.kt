package com.rejowan.numberconverter.presentation.home

import androidx.lifecycle.ViewModel
import com.rejowan.numberconverter.presentation.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val _currentTab = MutableStateFlow(Screen.Converter.route)
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    fun onTabSelected(route: String) {
        _currentTab.value = route
    }

    fun getTitleForRoute(route: String): String {
        return when (route) {
            Screen.Converter.route -> "Convert"
            Screen.Calculator.route -> "Calculate"
            Screen.Learn.route -> "Learn"
            Screen.Settings.route -> "Settings"
            else -> "Number Converter"
        }
    }
}
