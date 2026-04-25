package com.rejowan.numberconverter.presentation.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.rejowan.numberconverter.BuildConfig
import com.rejowan.numberconverter.presentation.common.theme.spacing
import com.rejowan.numberconverter.presentation.settings.components.DecimalPlacesDialog
import com.rejowan.numberconverter.presentation.settings.components.FontSizeDialog
import com.rejowan.numberconverter.presentation.settings.components.PreferenceItem
import com.rejowan.numberconverter.presentation.settings.components.SectionHeader
import com.rejowan.numberconverter.presentation.settings.components.ThemeDialog
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val spacing = spacing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Appearance Section
        SectionHeader(title = "Appearance")

        PreferenceItem(
            title = "Theme",
            summary = when (uiState.theme) {
                "light" -> "Light"
                "dark" -> "Dark"
                else -> "System default"
            },
            onClick = { viewModel.showThemeDialog() }
        )

        PreferenceItem(
            title = "Dynamic Colors",
            summary = "Use Material You colors (Android 12+)",
            checked = uiState.dynamicColors,
            onCheckedChange = { viewModel.updateDynamicColors(it) }
        )

        PreferenceItem(
            title = "Font Size",
            summary = uiState.fontSize.replaceFirstChar { it.uppercase() },
            onClick = { viewModel.showFontSizeDialog() }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

        // Converter Section
        SectionHeader(title = "Converter")

        PreferenceItem(
            title = "Decimal Places",
            summary = "${uiState.decimalPlaces} places",
            onClick = { viewModel.showDecimalPlacesDialog() }
        )

        PreferenceItem(
            title = "Auto-save History",
            summary = "Automatically save conversions to history",
            checked = uiState.autoSaveHistory,
            onCheckedChange = { viewModel.updateAutoSaveHistory(it) }
        )

        PreferenceItem(
            title = "Show Explanations",
            summary = "Display step-by-step explanations",
            checked = uiState.showExplanations,
            onCheckedChange = { viewModel.updateShowExplanations(it) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

        // Data & Privacy Section
        SectionHeader(title = "Data & Privacy")

        PreferenceItem(
            title = "Clear History",
            summary = "Delete all conversion history",
            onClick = { viewModel.clearHistory() }
        )

        PreferenceItem(
            title = "Reset Settings",
            summary = "Reset all settings to defaults",
            onClick = { viewModel.resetSettings() }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = spacing.small))

        // About Section
        SectionHeader(title = "About")

        PreferenceItem(
            title = "Version",
            summary = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
            onClick = { }
        )

        PreferenceItem(
            title = "Source Code",
            summary = "View on GitHub",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/rejowan/NumberConverter"))
                context.startActivity(intent)
            }
        )

        PreferenceItem(
            title = "License",
            summary = "Apache License 2.0",
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.apache.org/licenses/LICENSE-2.0"))
                context.startActivity(intent)
            }
        )
    }

    // Dialogs
    if (uiState.showThemeDialog) {
        ThemeDialog(
            currentTheme = uiState.theme,
            onThemeSelected = { theme ->
                viewModel.updateTheme(theme)
                viewModel.hideThemeDialog()
            },
            onDismiss = { viewModel.hideThemeDialog() }
        )
    }

    if (uiState.showFontSizeDialog) {
        FontSizeDialog(
            currentSize = uiState.fontSize,
            onSizeSelected = { size ->
                viewModel.updateFontSize(size)
                viewModel.hideFontSizeDialog()
            },
            onDismiss = { viewModel.hideFontSizeDialog() }
        )
    }

    if (uiState.showDecimalPlacesDialog) {
        DecimalPlacesDialog(
            currentPlaces = uiState.decimalPlaces,
            onPlacesSelected = { places ->
                viewModel.updateDecimalPlaces(places)
                viewModel.hideDecimalPlacesDialog()
            },
            onDismiss = { viewModel.hideDecimalPlacesDialog() }
        )
    }
}
