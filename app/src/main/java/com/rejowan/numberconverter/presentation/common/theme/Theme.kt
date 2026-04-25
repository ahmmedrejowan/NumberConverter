package com.rejowan.numberconverter.presentation.common.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * CompositionLocal exposing the active dark-mode state to descendants.
 */
val LocalIsDarkTheme = staticCompositionLocalOf { false }

// ============================================================================
// DARK COLOR SCHEME
// ============================================================================

private val DarkColorScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = DarkSurfaces.error,
    onError = DarkSurfaces.onError,
    errorContainer = DarkSurfaces.errorContainer,
    onErrorContainer = DarkSurfaces.onErrorContainer,
    background = DarkSurfaces.background,
    onBackground = DarkSurfaces.onBackground,
    surface = DarkSurfaces.surface,
    onSurface = DarkSurfaces.onSurface,
    surfaceVariant = DarkSurfaces.surfaceVariant,
    onSurfaceVariant = DarkSurfaces.onSurfaceVariant,
    outline = DarkSurfaces.outline,
    outlineVariant = DarkSurfaces.outlineVariant,
    scrim = DarkSurfaces.scrim,
    inverseSurface = DarkSurfaces.inverseSurface,
    inverseOnSurface = DarkSurfaces.inverseOnSurface,
    inversePrimary = inversePrimaryDark,
    surfaceDim = DarkSurfaces.surfaceDim,
    surfaceBright = DarkSurfaces.surfaceBright,
    surfaceContainerLowest = DarkSurfaces.surfaceContainerLowest,
    surfaceContainerLow = DarkSurfaces.surfaceContainerLow,
    surfaceContainer = DarkSurfaces.surfaceContainer,
    surfaceContainerHigh = DarkSurfaces.surfaceContainerHigh,
    surfaceContainerHighest = DarkSurfaces.surfaceContainerHighest,
)

// ============================================================================
// LIGHT COLOR SCHEME
// ============================================================================

private val LightColorScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = LightSurfaces.error,
    onError = LightSurfaces.onError,
    errorContainer = LightSurfaces.errorContainer,
    onErrorContainer = LightSurfaces.onErrorContainer,
    background = LightSurfaces.background,
    onBackground = LightSurfaces.onBackground,
    surface = LightSurfaces.surface,
    onSurface = LightSurfaces.onSurface,
    surfaceVariant = LightSurfaces.surfaceVariant,
    onSurfaceVariant = LightSurfaces.onSurfaceVariant,
    outline = LightSurfaces.outline,
    outlineVariant = LightSurfaces.outlineVariant,
    scrim = LightSurfaces.scrim,
    inverseSurface = LightSurfaces.inverseSurface,
    inverseOnSurface = LightSurfaces.inverseOnSurface,
    inversePrimary = inversePrimaryLight,
    surfaceDim = LightSurfaces.surfaceDim,
    surfaceBright = LightSurfaces.surfaceBright,
    surfaceContainerLowest = LightSurfaces.surfaceContainerLowest,
    surfaceContainerLow = LightSurfaces.surfaceContainerLow,
    surfaceContainer = LightSurfaces.surfaceContainer,
    surfaceContainerHigh = LightSurfaces.surfaceContainerHigh,
    surfaceContainerHighest = LightSurfaces.surfaceContainerHighest,
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    fontSize: String = "medium",
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val typography = getScaledTypography(fontSize)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing(),
        LocalIsDarkTheme provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = AppShapes,
            content = content
        )
    }
}

@Composable
private fun getScaledTypography(fontSize: String): Typography {
    val scaleFactor = when (fontSize) {
        "small" -> 0.9f
        "large" -> 1.1f
        else -> 1.0f
    }

    val baseline = AppTypography

    if (scaleFactor == 1.0f) return baseline

    return Typography(
        displayLarge = baseline.displayLarge.copy(fontSize = baseline.displayLarge.fontSize * scaleFactor),
        displayMedium = baseline.displayMedium.copy(fontSize = baseline.displayMedium.fontSize * scaleFactor),
        displaySmall = baseline.displaySmall.copy(fontSize = baseline.displaySmall.fontSize * scaleFactor),
        headlineLarge = baseline.headlineLarge.copy(fontSize = baseline.headlineLarge.fontSize * scaleFactor),
        headlineMedium = baseline.headlineMedium.copy(fontSize = baseline.headlineMedium.fontSize * scaleFactor),
        headlineSmall = baseline.headlineSmall.copy(fontSize = baseline.headlineSmall.fontSize * scaleFactor),
        titleLarge = baseline.titleLarge.copy(fontSize = baseline.titleLarge.fontSize * scaleFactor),
        titleMedium = baseline.titleMedium.copy(fontSize = baseline.titleMedium.fontSize * scaleFactor),
        titleSmall = baseline.titleSmall.copy(fontSize = baseline.titleSmall.fontSize * scaleFactor),
        bodyLarge = baseline.bodyLarge.copy(fontSize = baseline.bodyLarge.fontSize * scaleFactor),
        bodyMedium = baseline.bodyMedium.copy(fontSize = baseline.bodyMedium.fontSize * scaleFactor),
        bodySmall = baseline.bodySmall.copy(fontSize = baseline.bodySmall.fontSize * scaleFactor),
        labelLarge = baseline.labelLarge.copy(fontSize = baseline.labelLarge.fontSize * scaleFactor),
        labelMedium = baseline.labelMedium.copy(fontSize = baseline.labelMedium.fontSize * scaleFactor),
        labelSmall = baseline.labelSmall.copy(fontSize = baseline.labelSmall.fontSize * scaleFactor),
    )
}
