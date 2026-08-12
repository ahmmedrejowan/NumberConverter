package com.rejowan.numberconverter.presentation.settings

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Gavel
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Policy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.rejowan.numberconverter.BuildConfig
import com.rejowan.numberconverter.presentation.common.theme.SoftAccents
import com.rejowan.numberconverter.presentation.settings.components.ChangelogSheet
import com.rejowan.numberconverter.presentation.settings.components.ConfirmationSheet
import com.rejowan.numberconverter.presentation.settings.components.CreatorSheet
import com.rejowan.numberconverter.presentation.settings.components.DecimalPlacesSheet
import com.rejowan.numberconverter.presentation.settings.components.FontSizePickerSheet
import com.rejowan.numberconverter.presentation.settings.components.LicenseSheet
import com.rejowan.numberconverter.presentation.settings.components.OpenSourceLicensesSheet
import com.rejowan.numberconverter.presentation.settings.components.PrivacyPolicySheet
import com.rejowan.numberconverter.presentation.settings.components.SectionLabel
import com.rejowan.numberconverter.presentation.settings.components.SettingsHeaderCard
import com.rejowan.numberconverter.presentation.settings.components.SettingsOptionItem
import com.rejowan.numberconverter.presentation.settings.components.SettingsToggleItem
import com.rejowan.numberconverter.presentation.settings.components.ThemePickerSheet
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Sheet visibility — local Compose state, not VM state. The VM owns the
    // actual settings values; the screen owns transient UI state.
    var showThemeSheet by remember { mutableStateOf(false) }
    var showFontSizeSheet by remember { mutableStateOf(false) }
    var showDecimalPlacesSheet by remember { mutableStateOf(false) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }
    var showResetSettingsConfirm by remember { mutableStateOf(false) }
    var showChangelogSheet by remember { mutableStateOf(false) }
    var showPrivacyPolicySheet by remember { mutableStateOf(false) }
    var showLicenseSheet by remember { mutableStateOf(false) }
    var showOpenSourceLicensesSheet by remember { mutableStateOf(false) }
    var showCreatorSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 120.dp) // leave room for the floating bottom nav
        ) {
            SettingsHeaderCard(
                appName = "Number Converter",
                tagline = "Bin · Oct · Dec · Hex — explained step-by-step"
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================== APPEARANCE ==================
            SectionLabel(text = "APPEARANCE", delay = 0)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Palette,
                title = "Theme",
                subtitle = when (uiState.theme) {
                    "light" -> "Light mode"
                    "dark" -> "Dark mode"
                    else -> "System default"
                },
                accentColor = SoftAccents.Purple,
                onClick = { showThemeSheet = true },
                animationDelay = 50
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Rounded.AutoAwesome,
                title = "Dynamic Colors",
                subtitle = "Use Material You colors (Android 12+)",
                accentColor = SoftAccents.Pink,
                checked = uiState.dynamicColors,
                onCheckedChange = { viewModel.updateDynamicColors(it) },
                animationDelay = 100
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.FormatSize,
                title = "Font Size",
                subtitle = uiState.fontSize.replaceFirstChar { it.uppercase() },
                accentColor = SoftAccents.Teal,
                onClick = { showFontSizeSheet = true },
                animationDelay = 150
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================== CONVERTER ==================
            SectionLabel(text = "CONVERTER", delay = 200)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Numbers,
                title = "Decimal Places",
                subtitle = "${uiState.decimalPlaces} ${if (uiState.decimalPlaces == 1) "place" else "places"}",
                accentColor = SoftAccents.Blue,
                onClick = { showDecimalPlacesSheet = true },
                animationDelay = 250
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Rounded.Save,
                title = "Auto-save History",
                subtitle = "Save every conversion to history",
                accentColor = SoftAccents.Amber,
                checked = uiState.autoSaveHistory,
                onCheckedChange = { viewModel.updateAutoSaveHistory(it) },
                animationDelay = 300
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsToggleItem(
                icon = Icons.Rounded.Lightbulb,
                title = "Show Explanations",
                subtitle = "Display step-by-step conversion breakdowns",
                accentColor = SoftAccents.Green,
                checked = uiState.showExplanations,
                onCheckedChange = { viewModel.updateShowExplanations(it) },
                animationDelay = 350
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================== DATA & PRIVACY ==================
            SectionLabel(text = "DATA & PRIVACY", delay = 400)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.DeleteSweep,
                title = "Clear History",
                subtitle = "Remove every saved conversion",
                accentColor = SoftAccents.Red,
                onClick = { showClearHistoryConfirm = true },
                animationDelay = 450
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.RestartAlt,
                title = "Reset Settings",
                subtitle = "Restore all preferences to defaults",
                accentColor = SoftAccents.Red,
                onClick = { showResetSettingsConfirm = true },
                animationDelay = 500
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ================== ABOUT ==================
            SectionLabel(text = "ABOUT", delay = 550)
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Info,
                title = "Version ${BuildConfig.VERSION_NAME}",
                subtitle = "View changelog",
                accentColor = SoftAccents.Blue,
                onClick = { showChangelogSheet = true },
                animationDelay = 600
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Policy,
                title = "Privacy Policy",
                subtitle = "How your data is handled",
                accentColor = SoftAccents.Teal,
                onClick = { showPrivacyPolicySheet = true },
                animationDelay = 650
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Gavel,
                title = "App License",
                subtitle = "GNU GPL v3.0",
                accentColor = SoftAccents.Purple,
                onClick = { showLicenseSheet = true },
                animationDelay = 700
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Gavel,
                title = "Open Source Licenses",
                subtitle = "Libraries that power this app",
                accentColor = SoftAccents.Amber,
                onClick = { showOpenSourceLicensesSheet = true },
                animationDelay = 750
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Person,
                title = "Creator",
                subtitle = "About the developer",
                accentColor = SoftAccents.Pink,
                onClick = { showCreatorSheet = true },
                animationDelay = 800
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Code,
                title = "Source Code",
                subtitle = "View on GitHub",
                accentColor = SoftAccents.Green,
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, "https://github.com/ahmmedrejowan/Number-Converter-Compose".toUri())
                    )
                },
                animationDelay = 850
            )
            Spacer(modifier = Modifier.height(8.dp))

            SettingsOptionItem(
                icon = Icons.Rounded.Email,
                title = "Contact",
                subtitle = "Send feedback or report a bug",
                accentColor = SoftAccents.Blue,
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_SENDTO).apply {
                            data = "mailto:kmrejowan@gmail.com".toUri()
                            putExtra(Intent.EXTRA_SUBJECT, "Number Converter Feedback")
                        }
                    )
                },
                animationDelay = 900
            )
        }
    }

    // ================== SHEETS ==================

    if (showThemeSheet) {
        ThemePickerSheet(
            currentTheme = uiState.theme,
            onSelect = {
                viewModel.updateTheme(it)
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false }
        )
    }

    if (showFontSizeSheet) {
        FontSizePickerSheet(
            currentSize = uiState.fontSize,
            onSelect = {
                viewModel.updateFontSize(it)
                showFontSizeSheet = false
            },
            onDismiss = { showFontSizeSheet = false }
        )
    }

    if (showDecimalPlacesSheet) {
        DecimalPlacesSheet(
            currentPlaces = uiState.decimalPlaces,
            onSelect = {
                viewModel.updateDecimalPlaces(it)
                showDecimalPlacesSheet = false
            },
            onDismiss = { showDecimalPlacesSheet = false }
        )
    }

    if (showClearHistoryConfirm) {
        ConfirmationSheet(
            title = "Clear History?",
            message = "This will permanently delete every saved conversion. This action cannot be undone.",
            confirmLabel = "Clear",
            icon = Icons.Rounded.History,
            onConfirm = {
                viewModel.clearHistory()
                showClearHistoryConfirm = false
            },
            onDismiss = { showClearHistoryConfirm = false }
        )
    }

    if (showResetSettingsConfirm) {
        ConfirmationSheet(
            title = "Reset All Settings?",
            message = "This will restore every preference to its default value. Saved history is not affected.",
            confirmLabel = "Reset",
            icon = Icons.Rounded.RestartAlt,
            onConfirm = {
                viewModel.resetSettings()
                showResetSettingsConfirm = false
            },
            onDismiss = { showResetSettingsConfirm = false }
        )
    }

    if (showChangelogSheet) ChangelogSheet(onDismiss = { showChangelogSheet = false })
    if (showPrivacyPolicySheet) PrivacyPolicySheet(onDismiss = { showPrivacyPolicySheet = false })
    if (showLicenseSheet) LicenseSheet(onDismiss = { showLicenseSheet = false })
    if (showOpenSourceLicensesSheet) OpenSourceLicensesSheet(onDismiss = { showOpenSourceLicensesSheet = false })
    if (showCreatorSheet) CreatorSheet(onDismiss = { showCreatorSheet = false })
}
