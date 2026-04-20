package org.example.project.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object AppColors {
    private val _isDark = mutableStateOf(true)
    var isDark: Boolean
        get() = _isDark.value
        set(value) { _isDark.value = value }

    // Backgrounds & surfaces
    val DarkChocolate: Color get() = if (isDark) Color(0xFF1A0F0A) else Color(0xFFFFF8F0)
    val Espresso: Color get() = if (isDark) Color(0xFF2A1A12) else Color(0xFFF5EDE3)
    val WarmBrown: Color get() = if (isDark) Color(0xFF3D2B1E) else Color(0xFFE8DDD3)
    val CardSurface: Color get() = if (isDark) Color(0xFF4A3628) else Color(0xFFF0E6DB)
    val CardSurfaceLight: Color get() = if (isDark) Color(0xFF5C4638) else Color(0xFFE5D8CC)

    // Accents — shared
    val DeepRed = Color(0xFFB33A3A)
    val WarmRed = Color(0xFFCC4545)
    val BrightRed = Color(0xFFE05555)

    val Amber: Color get() = if (isDark) Color(0xFFE6A030) else Color(0xFFD08A18)
    val HoneyYellow = Color(0xFFF0C050)
    val LightGold = Color(0xFFF5D78E)

    val BurntOrange = Color(0xFFE07030)
    val WarmOrange = Color(0xFFE88840)

    // Text
    val TextPrimary: Color get() = if (isDark) Color(0xFFF5E6D8) else Color(0xFF2C1E17)
    val TextSecondary: Color get() = if (isDark) Color(0xFFB8A090) else Color(0xFF6B5B50)
    val TextMuted: Color get() = if (isDark) Color(0xFF887060) else Color(0xFF9A8A80)

    // UI
    val Divider: Color get() = if (isDark) Color(0xFF4A3828) else Color(0xFFE0D5C8)
    val Success = Color(0xFF66BB6A)
    val SuccessDark = Color(0xFF388E3C)

    val accentColors = listOf(DeepRed, BurntOrange, Color(0xFFE6A030), WarmRed, WarmOrange)

    val PremiumGradient: Brush get() = Brush.horizontalGradient(listOf(DeepRed, BurntOrange, Amber))
    val CardGradient: Brush get() = Brush.verticalGradient(listOf(CardSurface, WarmBrown))
    val BackgroundGradient: Brush get() = Brush.verticalGradient(listOf(DarkChocolate, Espresso, DarkChocolate))
}

private val WarmDarkScheme = darkColorScheme(
    primary = Color(0xFFE6A030),
    onPrimary = Color(0xFF1A0F0A),
    primaryContainer = Color(0xFF3D2B1E),
    onPrimaryContainer = Color(0xFFF0C050),
    secondary = AppColors.DeepRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5C2020),
    onSecondaryContainer = Color(0xFFF5E6D8),
    tertiary = AppColors.BurntOrange,
    onTertiary = Color.White,
    background = Color(0xFF1A0F0A),
    onBackground = Color(0xFFF5E6D8),
    surface = Color(0xFF2A1A12),
    onSurface = Color(0xFFF5E6D8),
    surfaceVariant = Color(0xFF3D2B1E),
    onSurfaceVariant = Color(0xFFB8A090),
    outline = Color(0xFF4A3828),
    outlineVariant = Color(0xFF4A3628),
)

private val WarmLightScheme = lightColorScheme(
    primary = Color(0xFFD08A18),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFF0D6),
    onPrimaryContainer = Color(0xFF5C3A00),
    secondary = AppColors.DeepRed,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDAD6),
    onSecondaryContainer = Color(0xFF410002),
    tertiary = AppColors.BurntOrange,
    onTertiary = Color.White,
    background = Color(0xFFFFF8F0),
    onBackground = Color(0xFF2C1E17),
    surface = Color(0xFFF5EDE3),
    onSurface = Color(0xFF2C1E17),
    surfaceVariant = Color(0xFFE8DDD3),
    onSurfaceVariant = Color(0xFF6B5B50),
    outline = Color(0xFFE0D5C8),
    outlineVariant = Color(0xFFF0E6DB),
)

@Composable
fun JokerFlashTheme(darkTheme: Boolean = true, content: @Composable () -> Unit) {
    AppColors.isDark = darkTheme
    MaterialTheme(
        colorScheme = if (darkTheme) WarmDarkScheme else WarmLightScheme,
        content = content
    )
}
