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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
)

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
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
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

    // Scale typography based on font size preference
    val typography = getScaledTypography(fontSize)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
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
        else -> 1.0f // medium
    }

    val baseline = AppTypography

    return if (scaleFactor == 1.0f) {
        baseline
    } else {
        Typography(
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
}
