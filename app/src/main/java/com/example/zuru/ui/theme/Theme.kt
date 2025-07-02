package com.example.zuru.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// -- Define Color Schemes for Each Theme --
private val DefaultLightColors = lightColorScheme(
    primary = Color(0xFF00897B), // Teal
    secondary = Color(0xFF4DB6AC),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val DefaultDarkColors = darkColorScheme(
    primary = Color(0xFF00897B),
    secondary = Color(0xFF4DB6AC),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val OceanBlueLightColors = lightColorScheme(
    primary = Color(0xFF0277BD),
    secondary = Color(0xFF4FC3F7),
    background = Color.White,
    surface = Color(0xFFF0F0F0),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val OceanBlueDarkColors = darkColorScheme(
    primary = Color(0xFF0277BD),
    secondary = Color(0xFF4FC3F7),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val ForestGreenLightColors = lightColorScheme(
    primary = Color(0xFF388E3C),
    secondary = Color(0xFF81C784),
    background = Color.White,
    surface = Color(0xFFF7F7F7),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val ForestGreenDarkColors = darkColorScheme(
    primary = Color(0xFF388E3C),
    secondary = Color(0xFF81C784),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

private val SunsetOrangeLightColors = lightColorScheme(
    primary = Color(0xFFF57C00),
    secondary = Color(0xFFFFB74D),
    background = Color.White,
    surface = Color(0xFFFDF6F0),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black
)

private val SunsetOrangeDarkColors = darkColorScheme(
    primary = Color(0xFFF57C00),
    secondary = Color(0xFFFFB74D),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)


// -- AppThemeIdentifier Enum --
//enum class AppThemeIdentifier(val id: String) {
//    DEFAULT("default"),
//    OCEAN_BLUE("ocean_blue"),
//    FOREST_GREEN("forest_green"),
//    SUNSET_ORANGE("sunset_orange");
//
//    companion object {
//        fun fromId(id: String): AppThemeIdentifier {
//            return AppThemeIdentifier.entries.find { it.id == id } ?: DEFAULT
//        }
//    }
//}


// -- ZuruAppTheme Composable --
@Composable
fun ZuruAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeIdentifier: AppThemeIdentifier = AppThemeIdentifier.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeIdentifier) {
        AppThemeIdentifier.DEFAULT -> if (darkTheme) DefaultDarkColors else DefaultLightColors
        AppThemeIdentifier.OCEAN_BLUE -> if (darkTheme) OceanBlueDarkColors else OceanBlueLightColors
        AppThemeIdentifier.FOREST_GREEN -> if (darkTheme) ForestGreenDarkColors else ForestGreenLightColors
        AppThemeIdentifier.SUNSET_ORANGE -> if (darkTheme) SunsetOrangeDarkColors else SunsetOrangeLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
//        shapes = Shapes,
        content = content
    )
}
