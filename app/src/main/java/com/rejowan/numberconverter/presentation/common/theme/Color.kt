package com.rejowan.numberconverter.presentation.common.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// DARK THEME - Purple/Violet
// ============================================================================

val primaryDark = Color(0xFF9181F4)
val onPrimaryDark = Color(0xFFFFFFFF)
val primaryContainerDark = Color(0xFF7B6BE0)
val onPrimaryContainerDark = Color(0xFFFFFFFF)

val secondaryDark = Color(0xFFB8A5E3)
val onSecondaryDark = Color(0xFF1E1530)
val secondaryContainerDark = Color(0xFF8B7BC7)
val onSecondaryContainerDark = Color(0xFFFFFFFF)

val tertiaryDark = Color(0xFF4DB6AC)
val onTertiaryDark = Color(0xFF003731)
val tertiaryContainerDark = Color(0xFF00897B)
val onTertiaryContainerDark = Color(0xFFFFFFFF)

val inversePrimaryDark = Color(0xFF9181F4)

// ============================================================================
// SHARED DARK SURFACE COLORS
// ============================================================================

object DarkSurfaces {
    val background = Color(0xFF0A0A0F)
    val onBackground = Color(0xFFE8E5F0)
    val surface = Color(0xFF0A0A0F)
    val onSurface = Color(0xFFE8E5F0)
    val surfaceVariant = Color(0xFF3D3A47)
    val onSurfaceVariant = Color(0xFFC9C5D4)
    val outline = Color(0xFF8C899A)
    val outlineVariant = Color(0xFF49464F)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFFE8E5F0)
    val inverseOnSurface = Color(0xFF2E2C35)
    val surfaceDim = Color(0xFF0A0A0F)
    val surfaceBright = Color(0xFF35333D)
    val surfaceContainerLowest = Color(0xFF050508)
    val surfaceContainerLow = Color(0xFF141318)
    val surfaceContainer = Color(0xFF1A191F)
    val surfaceContainerHigh = Color(0xFF242329)
    val surfaceContainerHighest = Color(0xFF2F2D35)
    val error = Color(0xFFD32F2F)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFFEF5350)
    val onErrorContainer = Color(0xFFFFFFFF)
}

// ============================================================================
// SHARED LIGHT SURFACE COLORS
// ============================================================================

object LightSurfaces {
    val background = Color(0xFFFFFBFE)
    val onBackground = Color(0xFF1C1B1F)
    val surface = Color(0xFFFFFBFE)
    val onSurface = Color(0xFF1C1B1F)
    val surfaceVariant = Color(0xFFE7E0EC)
    val onSurfaceVariant = Color(0xFF49454F)
    val outline = Color(0xFF79747E)
    val outlineVariant = Color(0xFFCAC4D0)
    val scrim = Color(0xFF000000)
    val inverseSurface = Color(0xFF313033)
    val inverseOnSurface = Color(0xFFF4EFF4)
    val surfaceDim = Color(0xFFDED8E1)
    val surfaceBright = Color(0xFFFFFBFE)
    val surfaceContainerLowest = Color(0xFFFFFFFF)
    val surfaceContainerLow = Color(0xFFF7F2FA)
    val surfaceContainer = Color(0xFFF3EDF7)
    val surfaceContainerHigh = Color(0xFFECE6F0)
    val surfaceContainerHighest = Color(0xFFE6E0E9)
    val error = Color(0xFFBA1A1A)
    val onError = Color(0xFFFFFFFF)
    val errorContainer = Color(0xFFFFDAD6)
    val onErrorContainer = Color(0xFF410002)
}

// ============================================================================
// LIGHT THEME - Purple/Violet
// ============================================================================

val primaryLight = Color(0xFF7B68EE)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFEDE7FF)
val onPrimaryContainerLight = Color(0xFF21005E)

val secondaryLight = Color(0xFF625B71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE8DEF8)
val onSecondaryContainerLight = Color(0xFF1D192B)

val tertiaryLight = Color(0xFF7D5260)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD8E4)
val onTertiaryContainerLight = Color(0xFF31111D)

val inversePrimaryLight = Color(0xFFD0BCFF)

// ============================================================================
// ONBOARDING COLORS
// Distinct from Linky/PdfReaderPro — anchored on the violet primary, with a
// teal mid-tone and a warm amber finish to fit a numerical/computational vibe.
// ============================================================================

object OnboardingColors {
    val Page1 = Color(0xFF6750A4)  // Deep Violet — Convert
    val Page2 = Color(0xFF26A69A)  // Teal — Calculator
    val Page3 = Color(0xFFEF6C00)  // Burnt Amber — History
}

// ============================================================================
// SOFT ACCENT COLORS — used for icon containers, chips, and section accents
// throughout the UI. Theme-aware (dark/light variants) — resolved at composition
// time via the `SoftAccents` getter in Theme.kt.
// ============================================================================

object SoftAccentsDark {
    val Blue = Color(0xFF64B5F6)
    val Purple = Color(0xFF9575CD)
    val Pink = Color(0xFFF06292)
    val Teal = Color(0xFF4DB6AC)
    val Amber = Color(0xFFFFB74D)
    val Green = Color(0xFF81C784)
    val Red = Color(0xFFE57373)
}

object SoftAccentsLight {
    val Blue = Color(0xFF1976D2)
    val Purple = Color(0xFF7E57C2)
    val Pink = Color(0xFFD81B60)
    val Teal = Color(0xFF00897B)
    val Amber = Color(0xFFF57C00)
    val Green = Color(0xFF2E7D32)
    val Red = Color(0xFFC62828)
}
