package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val M3Primary = Color(0xFF6750A4)
val M3OnPrimary = Color(0xFFFFFFFF)
val M3PrimaryContainer = Color(0xFFEADDFF)
val M3OnPrimaryContainer = Color(0xFF21005D)
val M3SecondaryContainer = Color(0xFFE8DEF8)
val M3OnSecondaryContainer = Color(0xFF1D192B)
val M3Surface = Color(0xFFF3EDF7)
val M3Background = Color(0xFFFDF7FF)
val M3Outline = Color(0xFFCAC4D0)
val M3TurnContainer = Color(0xFFD0E4FF)
val M3OnTurnContainer = Color(0xFF001D36)

val FeltEmeraldFrom = Color(0xFF003D33)
val FeltEmeraldTo = Color(0xFF00251A)
val TableBorderWood = Color(0xFF2E2E2E)

private val PolishColorScheme = lightColorScheme(
    primary = M3Primary,
    onPrimary = M3OnPrimary,
    primaryContainer = M3PrimaryContainer,
    onPrimaryContainer = M3OnPrimaryContainer,
    secondary = Color(0xFF625B71),
    secondaryContainer = M3SecondaryContainer,
    onSecondaryContainer = M3OnSecondaryContainer,
    tertiary = Color(0xFF7D5260),
    background = M3Background,
    surface = M3Surface,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    outline = M3Outline
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = PolishColorScheme,
        typography = Typography,
        content = content
    )
}

