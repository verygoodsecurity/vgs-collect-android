@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** VGS design system tokens shared by demo screens. */
object DemoColors {
    val Background = Color(0xFFFAFBFF)
    val Surface = Color(0xFFFFFFFF)
    val Ink = Color(0xFF002270)
    val Muted = Color(0xFF5F6A86)
    val Border = Color(0xFFD8DEEC)
    val FieldBorder = Color(0xFFB9C2D8)
    val Segment = Color(0xFFE9EDF7)
    val Accent = Color(0xFF2480FF)
    val Section = Color(0xFF815DFF)
    val HeaderSubtitle = Color(0xFF8DB8FF)
    val Sandbox = Color(0xFF67E4BD)

    val HeaderBrush = Brush.linearGradient(listOf(Color(0xFF0B1020), Color(0xFF2A1D6E), Color(0xFF4B2A9C)))
    val IconBrush = Brush.linearGradient(listOf(Accent, Color(0xFFDD57FF)))
}

@Composable
fun DemoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = DemoColors.Accent,
            background = DemoColors.Background,
            surface = DemoColors.Surface,
            onBackground = DemoColors.Ink,
            onSurface = DemoColors.Ink,
        ),
        content = content
    )
}
