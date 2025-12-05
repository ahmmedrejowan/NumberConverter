package com.rejowan.numberconverter.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.rejowan.numberconverter.data.local.datastore.PreferencesManager
import com.rejowan.numberconverter.presentation.common.theme.AppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val preferencesManager: PreferencesManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val theme by preferencesManager.theme.collectAsState(initial = "system")
            val dynamicColors by preferencesManager.dynamicColors.collectAsState(initial = true)
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
                ParentScreen()
            }
        }
    }
}
