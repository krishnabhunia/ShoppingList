package com.krishna.shoppinglist.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    secondary = GreenDark,
    background = Color.White,
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.Black,
    secondary = GreenLight,
    background = SurfaceDark,
    surface = CardDark,
    onBackground = OnDark,
    onSurface = OnDark
)

@Composable
fun ShoppingListTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = Typography(), content = content)
}
