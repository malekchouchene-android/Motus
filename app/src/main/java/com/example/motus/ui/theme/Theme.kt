package com.example.motus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val misplacedColor = Color(0xFFfeba27)
val correctColor = Color(0xFFCB533D)
val incorrectColor = Color(0xFF007BFD)

private val LightColorScheme = lightColorScheme(
    primary = correctColor,
    secondary = misplacedColor,
    tertiary = Color.Black,
    surface = Color.White,
    background = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.Black,
)

@Composable
fun MotusTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}