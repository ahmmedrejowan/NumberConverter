package com.rejowan.numberconverter.presentation.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation routes using Kotlin Serialization.
 * Each route is a @Serializable object or data class — the route key is the
 * fully-qualified class name, and any constructor params are passed through
 * `androidx.navigation.toRoute()` extraction without manual stringification.
 */

// ============ PARENT-LEVEL ROUTES ============

@Serializable
object Onboarding

@Serializable
object Home

// ============ BOTTOM-NAV ROUTES (nested under Home) ============

@Serializable
object Converter

@Serializable
object Calculator

@Serializable
object Settings
