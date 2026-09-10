package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PuterDarkColorScheme = darkColorScheme(
    primary = PuterBlueLight,
    onPrimary = Color(0xFF041E42),
    primaryContainer = PuterBlueContainer,
    onPrimaryContainer = Color(0xFFD2E3FC),
    secondary = PuterPurpleLight,
    onSecondary = Color(0xFF2A124D),
    secondaryContainer = Color(0xFF2E2248),
    onSecondaryContainer = Color(0xFFE9D8FD),
    tertiary = PuterEmeraldLight,
    onTertiary = Color(0xFF023215),
    tertiaryContainer = PuterEmeraldContainer,
    onTertiaryContainer = Color(0xFFC4EED0),
    background = ObsidianBg,
    onBackground = PuterTextPrimary,
    surface = ObsidianSurface,
    onSurface = PuterTextPrimary,
    surfaceVariant = ObsidianSurfaceElevated,
    onSurfaceVariant = PuterTextSecondary,
    outline = ObsidianBorder,
    outlineVariant = ObsidianBorderLight
)

private val PuterLightColorScheme = lightColorScheme(
    primary = PuterBlueDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E3FC),
    onPrimaryContainer = Color(0xFF041E42),
    secondary = PuterPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE9D8FD),
    onSecondaryContainer = Color(0xFF2A124D),
    tertiary = PuterEmerald,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC4EED0),
    onTertiaryContainer = Color(0xFF023215),
    background = Color(0xFFF6F8FA),
    onBackground = Color(0xFF1F2328),
    surface = Color.White,
    onSurface = Color(0xFF1F2328),
    surfaceVariant = Color(0xFFEAEEF2),
    onSurfaceVariant = Color(0xFF57606A),
    outline = Color(0xFFD0D7DE),
    outlineVariant = Color(0xFFE1E4E8)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Puter AI Builder is a sleek dark developer workspace by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) PuterDarkColorScheme else PuterLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
