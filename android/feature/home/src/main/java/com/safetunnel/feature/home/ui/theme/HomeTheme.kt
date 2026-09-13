package com.safetunnel.feature.home.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*;
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4),
    secondary = Color(0xFF625B71),
    tertiary = Color(0xFF7D5260),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color(0xFFFFFFFF),
    onErrorContainer = Color(0xFF410E0B),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E),
    inverseOnSurface = Color(0xFFF0EFF4),
    inverseSurface = Color(0xFF2C2B2F),
    inversePrimary = Color(0xFFD0BCFF),
    surfaceTint = Color(0xFF6750A4)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFFCCC2DC),
    tertiary = Color(0xFFEFB8C8),
    error = Color(0xFFF2B8B7),
    errorContainer = Color(0xFF8C1D18),
    onError = Color(0xFF601410),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C1B1F),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    inverseOnSurface = Color(0xFF1C1B1F),
    inverseSurface = Color(0xFF302E34),
    inversePrimary = Color(0xFF6750A4),
    surfaceTint = Color(0xFFD0BCFF)
)

@Composable
fun HomeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}