package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GhostShieldDarkScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = GhostPurple,
    onSecondary = Color(0xFF381E72),
    secondaryContainer = Color(0xFF4F378A),
    onSecondaryContainer = Color(0xFFEADDFF),
    tertiary = NeonGreen,
    onTertiary = Color(0xFF003919),
    background = StealthBlack,
    onBackground = PureWhite,
    surface = StealthSurface,
    onSurface = PureWhite,
    surfaceVariant = StealthSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StealthBorder
)

private val VanishColorScheme = darkColorScheme(
    primary = GhostPurpleLight,
    onPrimary = Color(0xFF2E004F),
    primaryContainer = Color(0xFF4A0E78),
    onPrimaryContainer = Color(0xFFF3E5F5),
    secondary = VanishGlow,
    onSecondary = Color(0xFF3B0032),
    secondaryContainer = Color(0xFF5C004F),
    onSecondaryContainer = Color(0xFFFFD8EC),
    tertiary = NeonAmber,
    onTertiary = Color(0xFF3E2723),
    background = VanishDark,
    onBackground = PureWhite,
    surface = Color(0xFF100E17),
    onSurface = PureWhite,
    surfaceVariant = Color(0xFF1B1726),
    onSurfaceVariant = Color(0xFFC4B5FD),
    outline = Color(0xFF3B2D54)
)

@Composable
fun GhostShieldTheme(
    isVanishMode: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (isVanishMode) VanishColorScheme else GhostShieldDarkScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
