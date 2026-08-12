package com.rejowan.numberconverter.presentation.settings.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Code
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.FormatSize
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Numbers
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.rejowan.numberconverter.BuildConfig
import com.rejowan.numberconverter.presentation.common.theme.SoftAccents
import kotlin.math.roundToInt

// ============================================================================
// SHEET SCAFFOLD
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BaseSheet(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        content()
    }
}

@Composable
private fun SheetHeader(
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = accentColor.copy(alpha = 0.12f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(20.dp),
                tint = accentColor
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============================================================================
// THEME PICKER
// ============================================================================

@Composable
fun ThemePickerSheet(
    currentTheme: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = SoftAccents.Purple
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            SheetHeader(
                icon = Icons.Rounded.Palette,
                title = "Choose Theme",
                description = "Select your preferred appearance",
                accentColor = accent
            )

            Spacer(modifier = Modifier.height(20.dp))

            val themes = listOf(
                ThemeOptionData("system", "System Default", "Follow device settings", Icons.Rounded.PhoneAndroid),
                ThemeOptionData("light", "Light", "Always light mode", Icons.Rounded.LightMode),
                ThemeOptionData("dark", "Dark", "Always dark mode", Icons.Rounded.DarkMode)
            )
            themes.forEachIndexed { index, opt ->
                SelectableOption(
                    title = opt.title,
                    description = opt.description,
                    icon = opt.icon,
                    accentColor = accent,
                    isSelected = currentTheme == opt.value,
                    onClick = { onSelect(opt.value) }
                )
                if (index != themes.lastIndex) Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

private data class ThemeOptionData(
    val value: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

// ============================================================================
// FONT SIZE PICKER
// ============================================================================

@Composable
fun FontSizePickerSheet(
    currentSize: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = SoftAccents.Teal
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            SheetHeader(
                icon = Icons.Rounded.FormatSize,
                title = "Font Size",
                description = "Adjust text size across the app",
                accentColor = accent
            )

            Spacer(modifier = Modifier.height(20.dp))

            val sizes = listOf(
                Triple("small", "Small", "Compact text"),
                Triple("medium", "Medium", "Default size"),
                Triple("large", "Large", "Easier to read")
            )
            sizes.forEachIndexed { index, (value, title, description) ->
                SelectableOption(
                    title = title,
                    description = description,
                    icon = null,
                    accentColor = accent,
                    isSelected = currentSize == value,
                    onClick = { onSelect(value) }
                )
                if (index != sizes.lastIndex) Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ============================================================================
// DECIMAL PLACES PICKER (slider)
// ============================================================================

@Composable
fun DecimalPlacesSheet(
    currentPlaces: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val accent = SoftAccents.Blue
    var sliderValue by remember(currentPlaces) { mutableFloatStateOf(currentPlaces.toFloat()) }

    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            SheetHeader(
                icon = Icons.Rounded.Numbers,
                title = "Decimal Places",
                description = "Precision for fractional conversions",
                accentColor = accent
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Big readout
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = accent.copy(alpha = 0.10f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = sliderValue.roundToInt().toString(),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                    Text(
                        text = if (sliderValue.roundToInt() == 1) "place" else "places",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                valueRange = 1f..30f,
                steps = 28
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("1", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("30", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancel") }

                Button(
                    onClick = { onSelect(sliderValue.roundToInt()) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accent)
                ) { Text("Apply") }
            }
        }
    }
}

// ============================================================================
// CONFIRMATION SHEET — destructive actions
// ============================================================================

@Composable
fun ConfirmationSheet(
    title: String,
    message: String,
    confirmLabel: String,
    cancelLabel: String = "Cancel",
    accentColor: Color = SoftAccents.Red,
    icon: ImageVector = Icons.Rounded.Warning,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) { Text(cancelLabel) }

                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) { Text(confirmLabel) }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ============================================================================
// CHANGELOG SHEET
// ============================================================================

@Composable
fun ChangelogSheet(onDismiss: () -> Unit) {
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Changelog",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            ChangelogEntry(
                version = BuildConfig.VERSION_NAME,
                date = "April 2026",
                changes = listOf(
                    "Complete redesign with Material 3 violet theme",
                    "Animated bottom navigation with cutout effect",
                    "Onboarding with morphing background shapes",
                    "Type-safe navigation with Kotlin Serialization",
                    "Bottom-sheet settings dialogs",
                    "About section with credits and license info"
                )
            )
        }
    }
}

@Composable
private fun ChangelogEntry(
    version: String,
    date: String,
    changes: List<String>
) {
    val accent = SoftAccents.Purple
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = accent.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "v$version",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accent,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        changes.forEach { change ->
            Row(modifier = Modifier.padding(start = 8.dp, bottom = 6.dp)) {
                Text(
                    text = "•",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = change,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

// ============================================================================
// PRIVACY POLICY SHEET
// ============================================================================

@Composable
fun PrivacyPolicySheet(onDismiss: () -> Unit) {
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Privacy Policy",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            PolicySection(
                title = "Data Collection",
                content = "Number Converter does not collect any personal data. All conversions, history, and preferences stay on your device."
            )
            PolicySection(
                title = "Network Access",
                content = "The app does not require an internet connection. No analytics, telemetry, or remote logging is performed."
            )
            PolicySection(
                title = "Local Storage",
                content = "Conversion history is stored in a local Room database; settings are stored in DataStore. Both can be cleared from the Settings screen at any time."
            )
            PolicySection(
                title = "Third-Party Services",
                content = "The app uses Google Fonts (via the Downloadable Fonts API) to load the Ubuntu typeface. This is the only network-touching code path and requires no personal data."
            )
        }
    }
}

@Composable
private fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============================================================================
// LICENSE SHEET (GNU GPL v3.0)
// ============================================================================

@Composable
fun LicenseSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "GNU General Public License v3.0",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Text(
                    text = "Copyright (C) ${java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)} K M Rejowan Ahmmed\n\n" +
                            "This program is free software: you can redistribute it and/or modify " +
                            "it under the terms of the GNU General Public License as published by " +
                            "the Free Software Foundation, either version 3 of the License, or " +
                            "(at your option) any later version.\n\n" +
                            "This program is distributed in the hope that it will be useful, " +
                            "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
                            "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the " +
                            "GNU General Public License for more details.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Permissions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LicenseTermItem("Commercial use")
                    LicenseTermItem("Modification")
                    LicenseTermItem("Distribution")
                    LicenseTermItem("Patent use")
                    LicenseTermItem("Private use")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // The copyleft half — this is what separates GPL from a permissive
            // licence, so it gets equal billing rather than a footnote.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Conditions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LicenseTermItem("Disclose source")
                    LicenseTermItem("License and copyright notice")
                    LicenseTermItem("Same license")
                    LicenseTermItem("State changes")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                "https://www.gnu.org/licenses/gpl-3.0.en.html".toUri()
                            )
                        )
                    },
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Text(
                    text = "View Full License",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun LicenseTermItem(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// ============================================================================
// CREATOR SHEET
// ============================================================================

@Composable
fun CreatorSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "About Creator",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "K M Rejowan Ahmmed",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Android Developer",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Text(
                text = "Building expressive, educational Android apps with Kotlin and Jetpack Compose. Number Converter is a learning tool for the four classic number bases — built with care.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CreatorLinkItem(
                        icon = Icons.Rounded.Email,
                        label = "Email",
                        value = "kmrejowan@gmail.com",
                        accentColor = SoftAccents.Amber,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_SENDTO).apply {
                                data = "mailto:kmrejowan@gmail.com".toUri()
                                putExtra(Intent.EXTRA_SUBJECT, "Number Converter Feedback")
                            })
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CreatorLinkItem(
                        icon = Icons.Rounded.Code,
                        label = "GitHub",
                        value = "github.com/ahmmedrejowan",
                        accentColor = SoftAccents.Purple,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, "https://github.com/ahmmedrejowan".toUri()))
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CreatorLinkItem(
                        icon = Icons.Rounded.Work,
                        label = "LinkedIn",
                        value = "linkedin.com/in/ahmmedrejowan",
                        accentColor = SoftAccents.Teal,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, "https://linkedin.com/in/ahmmedrejowan".toUri()))
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    CreatorLinkItem(
                        icon = Icons.Rounded.Language,
                        label = "Portfolio",
                        value = "rejowan.com",
                        accentColor = SoftAccents.Blue,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, "https://rejowan.com".toUri()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CreatorLinkItem(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = accentColor.copy(alpha = 0.12f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .size(18.dp),
                tint = accentColor
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

// ============================================================================
// OPEN SOURCE LICENSES SHEET
// ============================================================================

private data class LibraryEntry(
    val name: String,
    val author: String,
    val license: String,
    val url: String
)

@Composable
fun OpenSourceLicensesSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val libraries = remember {
        listOf(
            LibraryEntry("Jetpack Compose", "Google", "Apache 2.0", "https://developer.android.com/jetpack/compose"),
            LibraryEntry("Material Components", "Google", "Apache 2.0", "https://m3.material.io/"),
            LibraryEntry("Navigation Compose", "Google", "Apache 2.0", "https://developer.android.com/jetpack/compose/navigation"),
            LibraryEntry("Room Database", "Google", "Apache 2.0", "https://developer.android.com/training/data-storage/room"),
            LibraryEntry("DataStore", "Google", "Apache 2.0", "https://developer.android.com/topic/libraries/architecture/datastore"),
            LibraryEntry("Koin", "Kotzilla", "Apache 2.0", "https://insert-koin.io/"),
            LibraryEntry("Kotlin Coroutines", "JetBrains", "Apache 2.0", "https://github.com/Kotlin/kotlinx.coroutines"),
            LibraryEntry("Kotlinx Serialization", "JetBrains", "Apache 2.0", "https://github.com/Kotlin/kotlinx.serialization"),
            LibraryEntry("Splash Screen", "Google", "Apache 2.0", "https://developer.android.com/develop/ui/views/launch/splash-screen")
        )
    }

    BaseSheet(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Open Source Licenses",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Number Converter stands on the shoulders of these projects.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                libraries.forEach { lib ->
                    LibraryRow(
                        library = lib,
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, lib.url.toUri()))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryRow(library: LibraryEntry, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = library.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${library.author} • ${library.license}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

// ============================================================================
// SHARED — selectable option row used by theme/font pickers
// ============================================================================

@Composable
private fun SelectableOption(
    title: String,
    description: String,
    icon: ImageVector?,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) accentColor.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "Selected",
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
