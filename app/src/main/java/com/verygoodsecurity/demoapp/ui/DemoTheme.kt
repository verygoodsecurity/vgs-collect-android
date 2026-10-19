@file:Suppress("UsingMaterialAndMaterial3Libraries")

package com.verygoodsecurity.demoapp.ui

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** VGS design system colors. Exposed through [DemoTheme.colors]. */
@Immutable
data class DemoColors(
    val background: Color = Color(0xFFFAFBFF),
    val surface: Color = Color(0xFFFFFFFF),
    val ink: Color = Color(0xFF002270),
    val muted: Color = Color(0xFF5F6A86),
    val border: Color = Color(0xFFD8DEEC),
    val fieldBorder: Color = Color(0xFFB9C2D8),
    val segment: Color = Color(0xFFE9EDF7),
    val accent: Color = Color(0xFF2480FF),
    val section: Color = Color(0xFF815DFF),
    val headerSubtitle: Color = Color(0xFF8DB8FF),
    val sandbox: Color = Color(0xFF67E4BD),
    val onHeader: Color = Color.White,
    val success: Color = Color(0xFF1F9D6B),
    val error: Color = Color(0xFFD23A4E),
    val errorContainer: Color = Color(0xFFFDEAED),
    val code: Color = Color(0xFF0B1020),
    val onCode: Color = Color(0xFFD8DEEC),
    val accentDark: Color = Color(0xFF145FF5),
    val accentContainer: Color = Color(0xFFE7F0FF),
    val warning: Color = Color(0xFF96590B),
    val warningContainer: Color = Color(0xFFFDF3E3),
    val headerBrush: Brush = Brush.linearGradient(listOf(Color(0xFF0B1020), Color(0xFF2A1D6E), Color(0xFF4B2A9C))),
    val heroBrush: Brush = Brush.linearGradient(
        0f to Color(0xFF0B1020),
        0.38f to Color(0xFF16205A),
        0.72f to Color(0xFF2A1D6E),
        1f to Color(0xFF4B2A9C),
    ),
    /** Cyan glow in the top-right corner, drawn over [heroBrush]. */
    val heroGlow: Brush = object : ShaderBrush() {
        override fun createShader(size: Size): Shader = RadialGradientShader(
            center = Offset(size.width, 0f),
            radius = size.width * 0.9f,
            colors = listOf(Color(0x4741D8F1), Color.Transparent),
            colorStops = listOf(0f, 0.55f),
        )
    },
    val iconBrush: Brush = Brush.linearGradient(listOf(Color(0xFF2480FF), Color(0xFFDD57FF))),
)

/** Spacing scale. Use these instead of raw dp values for paddings and gaps. */
@Immutable
data class DemoSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val s: Dp = 8.dp,
    val sm: Dp = 10.dp,
    val m: Dp = 12.dp,
    val l: Dp = 16.dp,
    val xl: Dp = 20.dp,
)

/** Fixed component dimensions shared across screens. */
@Immutable
data class DemoSizes(
    val logo: DpSize = DpSize(32.dp, 36.dp),
    val chipHeight: Dp = 26.dp,
    val chipDot: Dp = 7.dp,
    val controlHeight: Dp = 36.dp,
    val cardMinHeight: Dp = 84.dp,
    val iconTile: Dp = 40.dp,
    val sectionTile: Dp = 32.dp,
    val sectionTileIcon: Dp = 18.dp,
    val badgeHeight: Dp = 20.dp,
    val progressIndicator: Dp = 18.dp,
    val progressStroke: Dp = 2.6.dp,
    val progressBar: Dp = 3.dp,
    val accentStrip: Dp = 4.dp,
    val elevation: Dp = 1.dp,
    val elevationRaised: Dp = 6.dp,
    val touchTarget: Dp = 44.dp,
    val codeMaxHeight: Dp = 360.dp,
)

@Immutable
data class DemoShapes(
    val small: CornerBasedShape = RoundedCornerShape(8.dp),
    val medium: CornerBasedShape = RoundedCornerShape(12.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp),
    val pill: CornerBasedShape = RoundedCornerShape(percent = 50),
)

/** Text styles without color; pair with a [DemoColors] entry at the call site. */
@Immutable
data class DemoTypography(
    val headline: TextStyle = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Black),
    val title: TextStyle = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Bold),
    val label: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold),
    val body: TextStyle = TextStyle(fontSize = 15.sp),
    val caption: TextStyle = TextStyle(fontSize = 12.sp),
    val overline: TextStyle = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold),
    val badge: TextStyle = TextStyle(fontSize = 10.5.sp, fontWeight = FontWeight.Bold),
    val field: TextStyle = TextStyle(fontSize = 15.sp, fontFamily = FontFamily.Monospace),
    val heading: TextStyle = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.ExtraBold),
    val button: TextStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
    val code: TextStyle = TextStyle(fontSize = 11.5.sp, lineHeight = 18.sp, fontFamily = FontFamily.Monospace),
)

private val LocalDemoColors = staticCompositionLocalOf { DemoColors() }
private val LocalDemoSpacing = staticCompositionLocalOf { DemoSpacing() }
private val LocalDemoShapes = staticCompositionLocalOf { DemoShapes() }
private val LocalDemoSizes = staticCompositionLocalOf { DemoSizes() }
private val LocalDemoTypography = staticCompositionLocalOf { DemoTypography() }

/** Accessors for the demo design tokens, mirroring the `MaterialTheme` API. */
object DemoTheme {

    val colors: DemoColors
        @Composable @ReadOnlyComposable get() = LocalDemoColors.current

    val spacing: DemoSpacing
        @Composable @ReadOnlyComposable get() = LocalDemoSpacing.current

    val shapes: DemoShapes
        @Composable @ReadOnlyComposable get() = LocalDemoShapes.current

    val sizes: DemoSizes
        @Composable @ReadOnlyComposable get() = LocalDemoSizes.current

    val typography: DemoTypography
        @Composable @ReadOnlyComposable get() = LocalDemoTypography.current
}

@Composable
fun DemoTheme(
    colors: DemoColors = DemoColors(),
    spacing: DemoSpacing = DemoSpacing(),
    shapes: DemoShapes = DemoShapes(),
    sizes: DemoSizes = DemoSizes(),
    typography: DemoTypography = DemoTypography(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalDemoColors provides colors,
        LocalDemoSpacing provides spacing,
        LocalDemoShapes provides shapes,
        LocalDemoSizes provides sizes,
        LocalDemoTypography provides typography,
    ) {
        // Bridge to Material so its components (text fields, ripples, ...) pick up the same tokens.
        MaterialTheme(
            colors = lightColors(
                primary = colors.accent,
                background = colors.background,
                surface = colors.surface,
                onBackground = colors.ink,
                onSurface = colors.ink,
            ),
            typography = Typography(
                h5 = typography.headline,
                subtitle1 = typography.title,
                subtitle2 = typography.label,
                body1 = typography.body,
                caption = typography.caption,
                overline = typography.overline,
            ),
            shapes = Shapes(small = shapes.small, medium = shapes.medium, large = shapes.large),
            content = content
        )
    }
}
