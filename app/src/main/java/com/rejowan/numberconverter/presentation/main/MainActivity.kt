package com.rejowan.numberconverter.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.presentation.common.theme.AppTheme
import com.rejowan.numberconverter.presentation.navigation.Home
import com.rejowan.numberconverter.presentation.navigation.Onboarding
import com.rejowan.numberconverter.presentation.navigation.ParentNavGraph
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferencesManager: PreferencesManager by inject()

    private var isReady by mutableStateOf(false)
    private var startDestination: Any by mutableStateOf(Home)

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Hold the splash screen until the onboarding flag has loaded — avoids
        // a blank flash between the splash hiding and the NavHost rendering.
        splashScreen.setKeepOnScreenCondition { !isReady }

        lifecycleScope.launch {
            val completed = preferencesManager.onboardingCompleted.first()
            startDestination = if (completed) Home else Onboarding
            isReady = true
        }

        setContent {
            val theme by preferencesManager.theme.collectAsState(initial = "system")
            val dynamicColors by preferencesManager.dynamicColors.collectAsState(initial = false)
            val fontSize by preferencesManager.fontSize.collectAsState(initial = "medium")

            val isDarkTheme = when (theme) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme()
            }

            AppTheme(
                darkTheme = isDarkTheme,
                dynamicColor = dynamicColors,
                fontSize = fontSize
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isReady) {
                        ParentNavGraph(startDestination = startDestination)
                    }
                }
            }
        }
    }
}
